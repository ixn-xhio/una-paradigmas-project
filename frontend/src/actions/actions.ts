export const setExecutionId = (value: number) => {
    return {
      type: "SET_EXECUTION_ID",
      value: value
    };
};


export const changeCode = (value: string) => {
    return {
        type: " CHANGE_CODE",
        value: value
    }
}