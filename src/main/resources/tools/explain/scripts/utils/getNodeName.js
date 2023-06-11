const getNodeName = (plan) => {
  if (!plan.operator || !plan.operator["#operator"]) {
    return null;
  }

  switch (plan.operator["#operator"]) {
    case "InitialProject":
      return "Project";

    case "InitialGroup":
      return "Group";

    default:
      return plan.operator["#operator"];
  }
};
