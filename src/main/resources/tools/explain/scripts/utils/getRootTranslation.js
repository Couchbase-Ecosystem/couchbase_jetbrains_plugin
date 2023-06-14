const getRootTranslation = (orientation) => (root) => {
  switch (orientation) {
    case Orientation.TopToBottom:
    case Orientation.BottomToTop:
      root.x0 = 50;
      root.y0 = 0;
      break;
    case Orientation.LeftToRight:
    case Orientation.RightToLeft:
    default:
      root.x0 = 50;
      root.y0 = 0;
      break;
  }
  return `translate(${root.x0},${root.y0})`;
};
