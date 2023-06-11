const getHeight = (node, lineHeight, compact) => {
  if (compact) {
    return PlanConfig.compactSize;
  }
  let numLines = 2;
  if (node && node.data && node.data.details)
    numLines += node.data.details.length;
  return lineHeight * numLines;
};
