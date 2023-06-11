const getTooltip = (node) => {
  var op = node.operator;

  if (!op || !op["#operator"]) return "";

  // get details about the op, to see if we have info for a tool tip
  var childFields = getNonChildFieldList(op);
  if (childFields.length === 0)
    // no fields, no tool tip
    return "";

  // we have some results, build the tooltip
  var result = "";
  result +=
    '<div class="row"><h5>' +
    op["#operator"] +
    '</h5></div><ul class="wb-explain-tooltip-list">';

  result += childFields;
  result += "</ul>";

  return result;
};
