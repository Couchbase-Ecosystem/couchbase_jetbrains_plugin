const getWidth = (node, compact) => {
  if (compact) {
    return PlanConfig.compactSize;
  }
  let maxWidth = 20; // leave at least this much space
  if (node.data && node.data.name && node.data.name.length > maxWidth)
    maxWidth = node.data.name.length;
  if (node.data && node.data.details)
    for (let i = 0; i < node.data.details.length; i++)
      if (node.data.details[i] && node.data.details[i].length > maxWidth)
        maxWidth = node.data.details[i].length;

  return maxWidth * 5; //allow 5 units for each character
};
