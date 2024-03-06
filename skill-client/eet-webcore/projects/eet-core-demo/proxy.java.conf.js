const PROXY_CONFIG = {
  "/skm-ws/*": {
    target: "http://localhost:8080",
    secure: false,
    loglevel: "debug",
  },

  "/api/*": {
    target: "http://localhost:8080/skm-ws/",
    secure: false,
    loglevel: "debug",
    onProxyRes: function (pr, req, res) {
      if (pr.headers["location"]) {
        const url = pr.headers["location"].replace(
          "http://127.0.0.1:8080/",
          "/"
        );
        pr.headers["location"] = url;
      }
    },
    onProxyReq: function (proxyReq, req, res) {
      proxyReq.setHeader("client-id", "skm-ws");
      proxyReq.setHeader("client-secret", "Ld8ypvn#GVbTsG7e");
      proxyReq.setHeader("X-Forwarded-For", "10.187.38.100");
    },
  },

  "/me": {
    target: "http://localhost:3000",
    secure: false,
    loglevel: "debug",
  },
  "/tree-data": {
    target: "http://localhost:3000",
    secure: false,
    loglevel: "debug",
  },
  "/admin/*": {
    target: "http://localhost:3000",
    secure: false,
    loglevel: "debug",
  },
};

module.exports = PROXY_CONFIG;
