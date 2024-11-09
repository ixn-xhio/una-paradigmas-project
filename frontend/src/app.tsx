import { declareSubApp, xarcV2 } from "@xarc/react";

export const client = declareSubApp({
  name: "client",
  getModule: () => import("./client"),
});

xarcV2.debug("app.tsx");
