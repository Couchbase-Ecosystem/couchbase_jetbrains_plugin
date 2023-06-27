function equal(a, b) {
  return JSON.stringify(a) === JSON.stringify(b);
}

function main(plan, ref) {
  const options = {
    lineHeight: 15,
    orientation: Orientation.RightToLeft,
    compact: false,
  };

  let prevPlan = null;
  let prevOptions;
  let lists;
  let zoomer = null;

  if (plan && ref && !(equal(prevPlan, plan) && equal(prevOptions, options))) {
    prevPlan = Object.assign({}, plan);
    prevOptions = Object.assign({}, options);

    while (ref.hasChildNodes()) {
      if (ref.lastChild) {
        ref.removeChild(ref.lastChild);
      }
    }

    lists = analyzePlan(plan, null);
    let nodes = convertN1QLPlanToPlanNodes(plan, null, lists);
    if (nodes) {
      let tree = makeSimpleTreeFromPlanNodes(nodes, null, "null");
      zoomer = makeD3TreeFromSimpleTree(tree, ref, options);
    }
  }
}
