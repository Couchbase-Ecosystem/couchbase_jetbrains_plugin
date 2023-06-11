const removeAllToolTips = (node) => () => {
  // get rid of any existing tooltips
  select(node).selectAll(".wb-explain-tooltip").remove();
};
