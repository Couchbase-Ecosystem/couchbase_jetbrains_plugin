const isString = (data) => {
  return typeof data === "string";
};

const analyzePlan = (plan, lists) => {
  if (!lists) {
    lists = {
      buckets: {},
      fields: {},
      indexes: {},
      aliases: [],
      total_time: 0.0,
      warnings: [],
    };
  }

  if (!plan || isString(plan)) return null;

  if (plan.operator) return analyzePlan(plan.operator, null);

  if (plan.plan && !plan["#operator"]) return analyzePlan(plan.plan, null);

  var operatorName = plan["#operator"];

  if (plan.hint_not_followed)
    lists.warnings.push("Hint not followed: " + plan.hint_not_followed);

  if (
    plan["#time"] ||
    (plan["#stats"] && (plan["#stats"].execTime || plan["#stats"].servTime))
  ) {
    var parsedValue = 0.0;
    if (plan["#time"]) parsedValue = convertTimeStringToFloat(plan["#time"]);
    else if (plan["#stats"]) {
      if (plan["#stats"].execTime)
        parsedValue += convertTimeStringToFloat(plan["#stats"].execTime);
      if (plan["#stats"].servTime)
        parsedValue += convertTimeStringToFloat(plan["#stats"].servTime);
    }

    plan["#time_normal"] = convertTimeFloatToFormattedString(parsedValue);
    plan["#time_absolute"] = parsedValue;
    lists.total_time += parsedValue;
  }

  if (operatorName === "Sequence" && plan["~children"]) {
    var initialAliasLen = lists.aliases.length;

    for (var i = 0; i < plan["~children"].length; i++) {
      if (plan["~children"][i]["#operator"] === "Fetch")
        lists.currentKeyspace = plan["~children"][i].keyspace;
      analyzePlan(plan["~children"][i], lists);
    }

    lists.aliases.length = initialAliasLen;
    return lists;
  } else if (operatorName === "Parallel" && plan["~child"]) {
    analyzePlan(plan["~child"], lists);
    return lists;
  } else if (
    operatorName === "Prepare" &&
    plan.prepared &&
    plan.prepared.operator
  ) {
    analyzePlan(plan.prepared.operator, lists);
    return lists;
  } else if (operatorName === "ExceptAll" || operatorName === "IntersectAll") {
    if (plan["first"]) analyzePlan(plan["first"], lists);
    if (plan["second"]) analyzePlan(plan["second"], lists);
    return lists;
  } else if (operatorName === "Merge") {
    if (plan.as) lists.aliases.push({ keyspace: plan.keyspace, as: plan.as });
    if (plan["delete"]) analyzePlan(plan["delete"], lists);
    if (plan["update"]) analyzePlan(plan["update"], lists);
    if (plan.keyspace) getFieldsFromExpressionWithParser(plan.keyspace, lists);
    if (plan.key) getFieldsFromExpressionWithParser(plan.key, lists);
    return lists;
  } else if (operatorName === "Authorize" && plan["~child"]) {
    analyzePlan(plan["~child"], lists);
    return lists;
  } else if (operatorName === "DistinctScan" && plan["scan"]) {
    analyzePlan(plan["scan"], lists);
    return lists;
  } else if (
    (operatorName === "Union" || operatorName === "UnionAll") &&
    plan["~children"]
  ) {
    for (let i = 0; i < plan["~children"].length; i++)
      analyzePlan(plan["~children"][i], lists);

    return lists;
  }

  //
  // The Order operator has an array of expressions
  //
  else if (operatorName === "Order")
    for (let i = 0; i < plan.sort_terms.length; i++) {
      getFieldsFromExpressionWithParser(plan.sort_terms[i].expr, lists);
    }
  //
  // the CreateIndex operator has keys, which are expressions we need to parse
  //
  else if (operatorName === "CreateIndex") {
    if (plan.keys && plan.keys.length)
      // CreateIndex keys are un-parsed N1QL expressions, we need to parse
      for (let i = 0; i < plan.keys.length; i++) {
        var parseTree = n1ql.parse(plan.keys[i].expr);

        // parse tree has array of array of strings, we will build
        if (parseTree && plan.keyspace)
          for (var p = 0; p < parseTree.length; p++) {
            for (var j = 0; j < parseTree[p].pathsUsed.length; j++) {
              if (parseTree[p].pathsUsed[j]) {
                var field = plan.keyspace;
                for (var k = 0; k < parseTree[p].pathsUsed[j].length; k++) {
                  field += "." + parseTree[p].pathsUsed[j][k];
                }

                lists.fields[field] = true;
              }
            }
          }
      }
  }

  //

  // for all other operators, certain fields will tell us stuff:
  //  - keyspace is a bucket name
  //  - index is an index name
  //  - condition is a string containing an expression, fields there are of the form (`keyspace`.`field`)
  //  - expr is the same as condition
  //  - on_keys is an expression
  //  - limit is an expression
  //  - group_keys is an array of fields

  if (plan.keyspace) lists.buckets[plan.keyspace] = true;
  if (plan.index && plan.keyspace)
    lists.indexes[plan.keyspace + "." + plan.index] = true;
  else if (plan.index) lists.indexes[plan.index] = true;
  if (plan.group_keys)
    for (let i = 0; i < plan.group_keys.length; i++)
      lists.fields[plan.group_keys[i]] = true;
  if (plan.condition) getFieldsFromExpressionWithParser(plan.condition, lists);
  if (plan.expr) getFieldsFromExpressionWithParser(plan.expr, lists);
  if (plan.on_keys) getFieldsFromExpressionWithParser(plan.on_keys, lists);
  if (plan.limit) getFieldsFromExpressionWithParser(plan.limit, lists);

  if (plan.as && plan.keyspace) {
    lists.aliases.push({ keyspace: plan.keyspace, as: plan.as });
    //console.log("Got alias " + plan.as + " for " + plan.keyspace);
  }
  if (plan.result_terms && Array.isArray(plan.result_terms))
    for (let i = 0; i < plan.result_terms.length; i++)
      if (plan.result_terms[i].expr)
        if (
          plan.result_terms[i].expr === "self" &&
          plan.result_terms[i].star &&
          lists.currentKeyspace
        )
          lists.fields[lists.currentKeyspace + ".*"] = true;
        else {
          getFieldsFromExpressionWithParser(plan.result_terms[i].expr, lists);
        }

  return lists;
};

function convertTimeStringToFloat(timeValue) {
  // regex for parsing time values like 3m23.7777s or 234.9999ms or 3.8888s
  // groups: 1: minutes, 2: secs, 3: millis, 4: microseconds
  var durationExpr =
    /(?:(\d+)h)?(?:(\d+)m)?(?:(\d+\.\d+)s)?(?:(\d+\.\d+)ms)?(?:(\d+\.\d+)µs)?/;
  var result = 0.0;

  var m = timeValue.match(durationExpr);
  //console.log(m[0]);

  if (m) {
    // minutes
    if (m[1])
      // hours value, should be an int
      result += parseInt(m[1]) * 3600;

    if (m[2])
      // minutes value, should be an int
      result += parseInt(m[2]) * 60;

    // seconds
    if (m[3]) result += parseFloat(m[3]);

    // milliseconds
    if (m[4]) result += parseFloat(m[4]) / 1000;

    // ooh, microseconds!
    if (m[5]) result += parseFloat(m[5]) / 1000000;
  }

  return result;
}

function convertTimeFloatToFormattedString(timeValue) {
  var minutes = 0;
  if (timeValue > 60) minutes = Math.floor(timeValue / 60);
  var seconds = timeValue - minutes * 60;

  var minutesStr = minutes.toString();
  if (minutesStr.length < 2) minutesStr = "0" + minutesStr;

  var secondsStr = (seconds < 10 ? "0" : "") + seconds.toString();
  if (secondsStr.length > 7) secondsStr = secondsStr.substring(0, 6);

  return minutesStr + ":" + secondsStr;
}

function getFieldsFromExpressionWithParser(expression, lists) {
  try {
    var parseTree = n1ql.parse(expression);

    // parse tree has array of array of strings, we will build
    if (parseTree) {
      for (var p = 0; p < parseTree.length; p++) {
        if (parseTree[p].pathsUsed) {
          for (var j = 0; j < parseTree[p].pathsUsed.length; j++) {
            //console.log("Got path p: " + p + ", j: " + j + ", " + JSON.stringify(parseTree[p].pathsUsed[j]));
            if (parseTree[p].pathsUsed[j]) {
              var field = "";
              for (var k = 0; k < parseTree[p].pathsUsed[j].length; k++) {
                var pathElement = parseTree[p].pathsUsed[j][k];

                // check for bucket aliases
                if (k === 0 && lists.aliases) {
                  for (var a = 0; a < lists.aliases.length; a++) {
                    if (lists.aliases[a].as === pathElement) {
                      pathElement = lists.aliases[a].keyspace;
                      break;
                    }
                  }
                }

                // first item in path should go into buckets
                if (k === 0) lists.buckets[pathElement] = true;

                field +=
                  (k > 0 && pathElement !== "[]" ? "." : "") + pathElement;
              }

              //console.log("  Got field: " + field);
              if (k > 1) lists.fields[field] = true;
            }
          }
        }
      }
    }
  } catch (e) {
    console.log("Error parsing: " + expression);
    console.log("Error: " + e);
  }
}
