const linkHorizontal = window.d3.linkHorizontal;
const linkVertical = window.d3.linkVertical;

const getLink = ({ orientation, lineHeight, compact }) => {
  switch (orientation) {
    case Orientation.TopToBottom:
      return linkVertical()
        .x((node) => {
          return node.x;
        })
        .y((node) => {
          return node.y + getHeight(node, lineHeight, compact) / 2;
        });
    case Orientation.BottomToTop:
      return linkVertical()
        .x((node) => {
          return node.x;
        })
        .y((node) => {
          return -node.y - getHeight(node, lineHeight, compact) / 2;
        });
    case Orientation.LeftToRight:
      return linkHorizontal()
        .x((node) => {
          return node.y + getWidth(node, compact) / 2;
        })
        .y((node) => {
          return node.x;
        });
    case Orientation.RightToLeft:
    default:
      return linkHorizontal()
        .x((node) => {
          return -node.y - getWidth(node, compact) / 2;
        })
        .y((node) => {
          return node.x;
        });
  }
};
