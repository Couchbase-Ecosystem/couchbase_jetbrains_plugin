const minNodeWidthVert = 155; // horizontal spacing for vertical trees
const minNodeWidth = 225;

const hierarchy = window.d3.hierarchy;
const tree = window.d3.tree;
const select = window.d3.select;
const zoom = window.d3.zoom;
const zoomIdentity = window.d3.zoomIdentity;

const makeD3TreeFromSimpleTree = (root, ele, options) => {
  if (!ele) {
    return null;
  }
  const { lineHeight, orientation } = options;
  const duration = 500;
  let i = 0;
  const vert = orientation === "TopToBottom" || orientation === "BottomToTop";

  const canvas_width = ele.clientWidth;
  const canvas_height = ele.clientHeight;

  const svg = select(ele)
    .append("svg")
    .attr("width", "100%")
    .attr("height", "100%")
    .style("overflow", "scroll")
    .on("click", removeAllToolTips(ele))
    .append("g")
    .attr("class", "drawarea")
    .attr("id", "svg_g");

  const trans = svg.transition().duration(duration);

  const arrowhead_refX = vert ? 0 : 0;
  const arrowhead_refY = vert ? 2 : 2;

  svg
    .append("defs")
    .append("marker")
    .attr("id", "arrowhead")
    .attr("viewBox", "0 0 10 12")
    .attr("refX", arrowhead_refX)
    .attr("refY", arrowhead_refY)
    .attr("markerWidth", 20)
    .attr("markerHeight", 20)
    .attr("orient", "auto")
    .append("path")
    .attr("d", "M 6 0 V 4 L 0 2 Z");

  const minNodeSize = vert
    ? [minNodeWidthVert, lineHeight * 7]
    : [lineHeight * 6, minNodeWidth];

  const d3Root = hierarchy(root);
  tree().nodeSize(minNodeSize)(d3Root);
  const diagonal = getLink(options);

  const nodes = d3Root.descendants();
  const links = d3Root.links();

  let minX = canvas_width;
  let maxX = 0;
  let minY = canvas_height;
  let maxY = 0;

  nodes.forEach(function (d) {
    minX = Math.min(d.x, minX);
    minY = Math.min(d.y, minY);
    maxX = Math.max(d.x, maxX);
    maxY = Math.max(d.y, maxY);
  });

  const dx = vert ? maxX - minX : maxY - minY;
  const dy = !vert ? maxX - minX : maxY - minY;
  let x = vert ? (minX + maxX) / 2 : (minY + maxY) / 2;
  let y = !vert ? (minX + maxX) / 2 : (minY + maxY) / 2;

  if (orientation === "Bottom to Top") y = -y;
  else if (orientation === "Right to Left") x = -x;

  const scale = Math.max(
    0.15,
    Math.min(2, 0.85 / Math.max(dx / canvas_width, dy / canvas_height))
  );
  const midX = canvas_width / 2 - scale * x,
    midY = canvas_height / 2 - scale * y;

  // set up zooming
  const zoomer = zoom()
    .scaleExtent([0.1, 2.5])
    .on("zoom", (event) => {
      svg.attr("transform", event.transform);
    });

  select(ele).call(zoomer);
  // set up the initial location of the graph, so it's centered on the screen
  select(ele)
    .transition()
    .call(zoomer.transform, zoomIdentity.translate(midX, midY).scale(scale));

  // Each node needs a unique id. If id field doesn't exist, use incrementing value
  const node = svg.selectAll("g.node").data(nodes, function (d) {
    return d.id || (d.id = ++i);
  });

  // Enter any new nodes at the parent's previous position.
  const nodeEnter = node
    .enter()
    .append("svg:g")
    .attr("class", "wb-explain-node")
    .attr("transform", getRootTranslation(orientation))
    .on("click", makeTooltip(event, ele));

  // *** node drop-shadows come from this filter ******************
  // filters go in defs element
  const defs = svg.append("defs");

  // create filter with id #drop-shadow
  // height=130% so that the shadow is not clipped
  const filter = defs
    .append("filter")
    .attr("id", "drop-shadow")
    .attr("height", "130%");

  // SourceAlpha refers to opacity of graphic that this filter will be applied to
  // convolve that with a Gaussian with standard deviation 1 and store result
  // in blur
  filter
    .append("feGaussianBlur")
    .attr("in", "SourceAlpha")
    .attr("stdDeviation", 1)
    .attr("result", "blur");

  // translate output of Gaussian blur downwards with 1px
  // store result in offsetBlur
  filter
    .append("feOffset")
    .attr("in", "blur")
    .attr("dx", 0)
    .attr("dy", 1)
    .attr("result", "offsetBlur");

  // overlay original SourceGraphic over translated blurred opacity by using
  // feMerge filter. Order of specifying inputs is important!
  const feMerge = filter.append("feMerge");

  feMerge.append("feMergeNode").attr("in", "offsetBlur");
  feMerge.append("feMergeNode").attr("in", "SourceGraphic");

  // ********* create node style from data *******************
  nodeEnter
    .append("rect")
    .attr("width", function (d) {
      return getWidth(d, options.compact);
    })
    .attr("height", function (d) {
      return getHeight(d, lineHeight, options.compact);
    })
    .attr("rx", lineHeight) // sets corner roundness
    .attr("ry", lineHeight)
    .attr("x", function (d) {
      return (-1 / 2) * getWidth(d, options.compact);
    }) // make the rect centered on our x/y coords
    .attr("y", function (d) {
      return (getHeight(d, lineHeight, options.compact) * -1) / 2;
    })
    .attr("class", function (d) {
      return d.data.level;
    })
    // drop-shadow filter
    .style("filter", "url(#drop-shadow)");

  if (!options.compact) {
    nodeEnter
      .append("text")
      .attr("dy", function (d) {
        return (
          (getHeight(d, lineHeight, options.compact) * -1) / 2 + lineHeight
        );
      }) // m
      .attr("class", "wb-explain-node-text")
      .text(function (d) {
        return d.data.name;
      });
  }

  const calcHeight = (_l, i) => (d) =>
    (getHeight(d, lineHeight, options.compact) * -1) / 2 + lineHeight * (i + 2);

  if (!options.compact) {
    const getText = (i) => (d) => d.data.details[i];

    // handle up to 4 lines of details
    for (let i = 0; i < 4; i++)
      nodeEnter
        .append("text")
        .attr("dy", calcHeight(lineHeight, i))
        .attr("class", "wb-explain-node-text-details")
        .text(getText(i));
  }
  // Transition nodes to their new position.
  nodeEnter
    .transition(trans)
    .attr("transform", getNodeTranslation(orientation));

  //Transition exiting nodes to the parent's new position.
  const nodeExit = node
    .exit()
    .transition(trans)
    .attr("transform", () => {
      return "translate(" + d3Root.y + "," + d3Root.x + ")";
    })
    .remove();

  nodeExit.select("rect").attr("r", 1e-6);

  // Update the links…
  const link = svg.selectAll("path.link").data(links, function (d) {
    return d.target.id;
  });

  // Enter any new links at the parent's previous position.
  link
    .enter()
    .insert("path", "g")
    .attr("class", function (l) {
      // clone nodes are duplicates. if we have a link between 2, we need to hide it
      if (l.target.cloneOf && l.source.cloneOf) return "wb-clone-link";
      else return "wb-explain-link";
    })
    .attr("marker-start", "url(#arrowhead)")
    .attr("d", () => {
      const o = { x: d3Root.x0, y: d3Root.y0 };
      const p = diagonal({ source: o, target: o });
      return p;
    })
    // Transition links to their new position.
    .transition(trans)
    .attr("d", diagonal);

  // Transition exiting nodes to the parent's new position.
  link
    .exit()
    .transition(trans)
    .attr("d", () => {
      const o = { x: d3Root.x, y: d3Root.y };
      return diagonal({ root: o, target: o });
    })
    .remove();

  // Stash the old positions for transition.
  nodes.forEach(function (d) {
    d.x0 = d.x;
    d.y0 = d.y;
  });

  return zoomer;
};
