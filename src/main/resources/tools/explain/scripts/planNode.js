const makePlanNode = (predecessor, operator, subsequence, total_query_time) => {
  return {
    predecessor: predecessor, // might be an array if this is a Union node
    operator: operator, // object from the actual plan
    subsequence: subsequence, // for parallel ops, arrays of plan nodes done in parallel
    time_percent:
      total_query_time && operator["#time_absolute"]
        ? Math.round((operator["#time_absolute"] * 1000) / total_query_time) /
          10
        : undefined,
  };
};
