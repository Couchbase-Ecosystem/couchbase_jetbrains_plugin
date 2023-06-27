const childFieldNames =
  /#operator|~child*|delete|update|scans|first|second|inputs/;

const getNonChildFieldList = (op) => {
  let result = "";

  for (const field in op) {
    if (!field.match(childFieldNames)) {
      const val = op[field];
      result += "<li>" + field;

      if (
        typeof val === "string" ||
        typeof val === "number" ||
        typeof val === "boolean"
      )
        result += " - " + val;
      else if (Array.isArray(val)) {
        result += "<ul>";
        for (let i = 0; i < val.length; i++) {
          if (typeof val[i] === "string") {
            result += "<li>" + val[i] + "</li>";
          } else {
            result += getNonChildFieldList(val[i]);
          }
        }
        result += "</ul>";
      } else if (typeof val === "object" && typeof val !== "function") {
        result += "<ul>";
        result += getNonChildFieldList(val);
        result += "</ul>";
      }
      result += "</li>";
    }
  }
  return result;
};
