const MAX_LENGTH = 35;

const concatTruncated = (array, item) => {
  const g = array.push(truncate(MAX_LENGTH, item));
  return g;
};

const truncate = (length, item) => {
  if (typeof item !== "string") return item;

  if (item.length > length) return item.slice(0, length) + "...";
  else return item;
};

const getNodeDetails = (plan) => {
  const result = [];
  const op = plan.operator;

  if (!op || !op["#operator"]) return result;

  switch (op["#operator"]) {
    case "IndexScan":
      concatTruncated(result, "by: " + op.keyspace + "." + op.index);
      break;

    case "IndexScan2":
    case "IndexScan3":
      concatTruncated(result, op.keyspace + "." + op.index);
      if (op.as) concatTruncated(result, "as: " + op.as);
      break;

    case "PrimaryScan":
      concatTruncated(result, op.keyspace);
      break;

    case "InitialProject":
      concatTruncated(result, op.result_terms.length + " terms");
      break;

    case "Fetch":
      concatTruncated(result, op.keyspace + (op.as ? " as " + op.as : ""));
      break;

    case "Alias":
      concatTruncated(result, op.as);
      break;

    case "NestedLoopJoin":
    case "NestedLoopNest":
    case "HashJoin":
    case "HashNest":
      concatTruncated(result, "on: " + op.on_clause);
      break;

    case "Limit":
    case "Offset":
      concatTruncated(result, op.expr);
      break;

    case "Join":
      concatTruncated(
        result,
        op.keyspace + (op.as ? " as " + op.as : "") + " on " + op.on_keys
      );
      break;

    case "Order":
      if (op.sort_terms)
        for (let i = 0; i < op.sort_terms.length; i++)
          concatTruncated(result, op.sort_terms[i].expr);
      break;

    case "InitialGroup":
    case "IntermediateGroup":
    case "FinalGroup":
      if (op.aggregates && op.aggregates.length > 0) {
        let aggr = "Aggrs: ";
        for (let i = 0; i < op.aggregates.length; i++) aggr += op.aggregates[i];
        concatTruncated(result, aggr);
      }

      if (op.group_keys && op.group_keys.length > 0) {
        let keys = "By: ";
        for (let i = 0; i < op.group_keys.length; i++) keys += op.group_keys[i];
        concatTruncated(result, keys);
      }
      break;

    case "Filter":
      if (op.condition) concatTruncated(result, op.condition);
      break;
  }

  if (op.limit && op.limit.length) concatTruncated(result, op.limit);

  if (op["#time_normal"]) {
    concatTruncated(
      result,
      op["#time_normal"] +
        (plan.time_percent && plan.time_percent > 0
          ? " (" + plan.time_percent + "%)"
          : "")
    );
  }

  if (op["#stats"]) {
    let inStr = "";
    let outStr = "";

    if (op["#stats"]["#itemsIn"] || op["#stats"]["#itemsIn"] === 0)
      inStr = op["#stats"]["#itemsIn"].toString();
    if (op["#stats"]["#itemsOut"] || op["#stats"]["#itemsOut"] === 0)
      outStr = op["#stats"]["#itemsOut"].toString();

    const inOutStr =
      (inStr.length > 0 ? inStr + " in" : "") +
      (inStr.length > 0 && outStr.length > 0 ? " / " : "") +
      (outStr.length > 0 ? outStr + " out" : "");

    if (inOutStr.length > 0) concatTruncated(result, inOutStr);
  }

  if (op["variables"]) concatTruncated(result, "vars: " + op["variables"]);
  if (op["expressions"]) concatTruncated(result, "expr:" + op["expressions"]);

  return result;
};
