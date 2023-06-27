const getNodeTranslation = (orientation) => (node) => {
  // if the node is a clone, get the translation from the source
  if (node.cloneOf) return getNodeTranslation(orientation)(node.cloneOf);

  // otherwise base it on the orientation of the graph
  switch (orientation) {
    case Orientation.TopToBottom:
      return "translate(" + node.x + "," + node.y + ")";

    case Orientation.BottomToTop:
      return "translate(" + node.x + "," + -node.y + ")";

    case Orientation.LeftToRight:
      return "translate(" + node.y + "," + node.x + ")";

    case Orientation.RightToLeft:
    default:
      return "translate(" + -node.y + "," + node.x + ")";
  }
};
