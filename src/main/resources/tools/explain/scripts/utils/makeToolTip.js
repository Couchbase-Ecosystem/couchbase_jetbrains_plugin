const makeTooltip = (node) => (event, d) => {
  removeAllToolTips(node)();

  // create the new tooltip
  const tooltip_div = select(node)
    .append("div")
    .attr("id", "svg_tooltip" + d.id)
    .attr("class", "wb-explain-tooltip")
    .on("click", function () {
      return tooltip_div.style("display", "none");
    });

  if (d.data.tooltip && d.data.tooltip.length > 0) {
    tooltip_div.transition().duration(300).style("display", "block");
    const header_div = tooltip_div.append("div");
    header_div.html(
      '<a class="ui-dialog-titlebar-close modal-close" onclick="console.log("click")"> X </a>'
    );

    tooltip_div
      .html(d.data.tooltip)
      .style("z-index", "30")
      .style("left", `${event.x}px`)
      .style("top", `${event.y - (document.getElementById("svg_tooltip" + d.id).offsetHeight / 2)}px`);
  }

  // todo: tooltip is not being appended to the DOM

  event.stopPropagation();
};
