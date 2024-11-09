const ide = (
  store = {
      id: 0,
      code: ""
    }, 
    action
) => {
  if (action.type === "SET_EXECUTION_ID") {
    return {
      id: action.value,
      ...store
    };
  } else if (action.type === "CHANGE_CODE") {
    return {
      code: action.value,
      ...store
    };
  }

  return store;
};

export const reduxReducers = {
  ide,
};
