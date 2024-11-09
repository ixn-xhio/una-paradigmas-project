const project = (store = { code: "", session_id: "" }, action) => {
  if (action.type === "CHANGE_CODE") {
    return {
      code: action.payload,
      session_id: store.session_id
    };
  } else if (action.type === "CHANGE_SESSION_ID") {
    return {
      session_id: action.payload,
      code: store.code
    };
  }
  return store;
};

export const reduxReducers = {
  project,
};
