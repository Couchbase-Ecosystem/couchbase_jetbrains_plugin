const convertN1QLPlanToPlanNodes = (plan, predecessor, lists) => {
  // sanity check
  if (!plan || typeof plan === "string") return null;

  // special case: prepared queries
  if (plan.operator)
    return convertN1QLPlanToPlanNodes(plan.operator, null, lists);

  // special case #2: plan with query timings is wrapped in an outer object
  if (plan.plan && !plan["#operator"])
    return convertN1QLPlanToPlanNodes(plan.plan, null, lists);

  var operatorName = plan["#operator"];

  if (!operatorName) {
    return null;
  }

  if (operatorName === "Sequence" && plan["~children"]) {
    for (var i = 0; i < plan["~children"].length; i++)
      predecessor = convertN1QLPlanToPlanNodes(
        plan["~children"][i],
        predecessor,
        lists
      );

    return predecessor;
  } else if (operatorName === "Parallel" && plan["~child"]) {
    var subsequence = convertN1QLPlanToPlanNodes(
      plan["~child"],
      predecessor,
      lists
    );

    for (
      var subNode = subsequence;
      subNode != null;
      subNode = subNode.predecessor
    ) {
      if (subNode === subsequence) subNode.parallelBegin = true;
      if (subNode.predecessor === predecessor) {
        subNode.parallelEnd = true;
      }
      subNode.parallel = true;
    }

    return subsequence;
  } else if (
    operatorName === "Prepare" &&
    plan.prepared &&
    plan.prepared.operator
  ) {
    return convertN1QLPlanToPlanNodes(plan.prepared.operator, null, lists);
  } else if (operatorName === "ExceptAll" || operatorName === "IntersectAll") {
    const children = [];

    if (plan["first"])
      children.push(convertN1QLPlanToPlanNodes(plan["first"], null, lists));

    if (plan["second"])
      children.push(convertN1QLPlanToPlanNodes(plan["second"], null, lists));

    if (children.length > 0)
      return makePlanNode(children, plan, null, lists.total_time);
    else return null;
  } else if (operatorName === "Merge") {
    const children = [];

    if (predecessor) children.push(predecessor);

    if (plan["insert"])
      children.push(convertN1QLPlanToPlanNodes(plan["insert"], null, lists));

    if (plan["delete"])
      children.push(convertN1QLPlanToPlanNodes(plan["delete"], null, lists));

    if (plan["update"])
      children.push(convertN1QLPlanToPlanNodes(plan["update"], null, lists));

    if (children.length > 0)
      return makePlanNode(children, plan, null, lists.total_time);
    else return null;
  } else if (operatorName === "Authorize" && plan["~child"]) {
    var authorizeNode = makePlanNode(predecessor, plan, null, lists.total_time);
    var authorizeChildren = convertN1QLPlanToPlanNodes(
      plan["~child"],
      authorizeNode,
      lists
    );
    return authorizeChildren;
  } else if (operatorName === "DistinctScan" && plan["scan"]) {
    return makePlanNode(
      convertN1QLPlanToPlanNodes(plan["scan"], null, lists),
      plan,
      null,
      lists.total_time
    );
  } else if (operatorName === "UnionAll" && plan["~children"]) {
    var unionChildren = [];

    for (let i = 0; i < plan["~children"].length; i++)
      unionChildren.push(
        convertN1QLPlanToPlanNodes(plan["~children"][i], predecessor, lists)
      );

    var unionNode = makePlanNode(unionChildren, plan, null, lists.total_time);

    return unionNode;
  } else if (
    (operatorName === "NestedLoopJoin" ||
      operatorName === "NestedLoopNest" ||
      operatorName === "HashJoin" ||
      operatorName === "HashNest" ||
      operatorName === "Join" ||
      operatorName === "Nest") &&
    plan["~child"]
  ) {
    var inner = predecessor;
    var outer = convertN1QLPlanToPlanNodes(plan["~child"], null, lists);
    return makePlanNode([inner, outer], plan, null, lists.total_time);
  } else if (operatorName === "UnionScan" || operatorName === "IntersectScan") {
    var scanChildren = [];

    for (let i = 0; i < plan["scans"].length; i++)
      scanChildren.push(
        convertN1QLPlanToPlanNodes(plan["scans"][i], null, lists)
      );

    return makePlanNode(scanChildren, plan, null, lists.total_time);
  } else if (
    operatorName === "FinalProject" ||
    operatorName === "IntermediateGroup" ||
    operatorName === "FinalGroup"
  ) {
    return predecessor;
  } else if (operatorName === "With") {
    var withNode = makePlanNode(predecessor, plan, null, lists.total_time);
    var withChildren = convertN1QLPlanToPlanNodes(
      plan["~child"],
      withNode,
      lists
    );
    return withChildren;
  } else {
    return makePlanNode(predecessor, plan, null, lists.total_time);
  }
};
