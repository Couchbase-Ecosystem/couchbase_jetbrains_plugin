const makeSimpleTreeFromPlanNodes = (plan, next, parent, nodeCache) => {
  if (!nodeCache) nodeCache = {};

  if (plan.subsequence)
    return makeSimpleTreeFromPlanNodes(
      plan.subsequence,
      plan.predecessor,
      parent,
      nodeCache
    );

  if (!plan || !plan.operator) return null;

  const details = getNodeDetails(plan);
  var result = {
    name: getNodeName(plan),
    details,
    parent: parent,
    level: "node",
    time: plan.time,
    time_percent: plan.time_percent,
    tooltip: getTooltip(plan),
    cloneOf: null,
    children: [],
  };

  if (plan && plan.time_percent) {
    if (plan.time_percent >= 20) result.level = "wb-explain-node-expensive-1";
    else if (plan.time_percent >= 5)
      result.level = "wb-explain-node-expensive-2";
    else if (plan.time_percent >= 1)
      result.level = "wb-explain-node-expensive-3";
  }

  if (
    plan &&
    plan.operator &&
    plan.operator.operatorId &&
    nodeCache[plan.operator.operatorId]
  )
    result.cloneOf = nodeCache[plan.operator.operatorId];
  else if (plan.operator.operatorId)
    nodeCache[plan.operator.operatorId] = result;

  if (plan.predecessor) {
    result.children = [];
    if (!Array.isArray(plan.predecessor)) {
      result.children.push(
        makeSimpleTreeFromPlanNodes(
          plan.predecessor,
          next,
          result.name,
          nodeCache
        )
      );
    } else {
      for (var i = 0; i < plan.predecessor.length; i++) {
        result.children.push(
          makeSimpleTreeFromPlanNodes(
            plan.predecessor[i],
            null,
            result.name,
            nodeCache
          )
        );
      }
    }
  }

  return result;
};
