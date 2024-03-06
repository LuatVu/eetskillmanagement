
export const CONSTANT_V_MODEL = {
  // Left
  REQUIREMENT_ENGINEERING : "requirementEngineering",
  SYSTEM_ARCHITECTURE : "systemArchitecture",
  SOFTWARE_ARCHITECTURE : "softwareArchitecture",
  SOFTWARE_DOCUMENTATION : "softwareDocumentation",
  SOFTWARE_MODEL_BASED_DEVELOPMENT : "softwareModelBasedDevelopment",
  SOFTWARE_RAPID_PROTOTYPING : "softwareRapidPrototyping",
  GENERIC_REVIEW: "genericReview",
  // right
  SYSTEM_SOFTWARE_TEST: "systemSoftwareTest",
  SYSTEM_INTEGRATION: "systemIntegration",
  SOFTWARE_INTEGRATION : "softwareIntegration",
  SOFTWARE_UNIT_TEST: "softwareUnitTest",
  SOFTWARE_CONSTRUCTION: "softwareConstruction",
  CI_DASHBOARD:"ciDashboard",
  // management/support tools
  PRJ_CONFIG_MANAGEMENT : "prjConfigManagement",
  PRJ_PLAN_CREATION_DELI: "prjPlanCreationDeli",
  SW_CONFIG_MANAGEMENT: "swConfigManagement",
  CHANGE_REQUEST: "changeRequest",
}
export const LOCATION_CHILD: any = {
  // CONTENT LEFT
  [CONSTANT_V_MODEL.REQUIREMENT_ENGINEERING] : {
    ROW_1 : {
      x : 220,
      y : 70
    },
    ROW_2 : {
      x : 220,
      y : 105
    }
  },
  [CONSTANT_V_MODEL.SYSTEM_ARCHITECTURE] : {
    ROW_1 : {
      x : 220,
      y : 180
    },
    ROW_2 : {
      x : 220,
      y : 215
    }
  },
  [CONSTANT_V_MODEL.SOFTWARE_ARCHITECTURE] : {
    ROW_1 : {
      x : 220,
      y : 338
    },
    // Y + 25
    ROW_2 : {
      x : 220,
      y : 363
    }
  },
  [CONSTANT_V_MODEL.SOFTWARE_DOCUMENTATION] : {
    ROW_1 : {
      x : 220,
      y : 422 
    },
    // + 27
    ROW_2 : {
      x : 220,
      y : 447 
    }
  },
  [CONSTANT_V_MODEL.SOFTWARE_MODEL_BASED_DEVELOPMENT] : {
    ROW_1 : {
      x : 220,
      y : 507
    },
    // Y + 27
    ROW_2 : {
      x : 220,
      y : 534
    }
  },
  [CONSTANT_V_MODEL.SOFTWARE_RAPID_PROTOTYPING] : {
    ROW_1 : {
      x : 220,
      y : 592
    },
    // Y + 27
    ROW_2 : {
      x : 220,
      y : 619
    }
  },
  [CONSTANT_V_MODEL.GENERIC_REVIEW] : {
    ROW_1 : {
      x : 220,
      y : 677
    },
    // Y + 27
    ROW_2 : {
      x : 220,
      y : 105
    }
  },
  // CONTENT RIGHT
  [CONSTANT_V_MODEL.SYSTEM_SOFTWARE_TEST] : {
    ROW_1 : {
      x : 1010,
      y : 70
    },
    ROW_2 : {
      x : 1010,
      y : 105
    }
  },
  [CONSTANT_V_MODEL.SYSTEM_INTEGRATION] : {
    ROW_1 : {
      x : 1010,
      y : 180
    },
    ROW_2 : {
      x : 1010,
      y : 215
    }
  },
  [CONSTANT_V_MODEL.SOFTWARE_INTEGRATION] : {
    ROW_1 : {
      x : 1010,
      y : 340
    },
    ROW_2 : {
      x : 1010,
      y : 375
    },
    ROW_3 : {
      x : 1010,
      y : 410
    }
  },
  [CONSTANT_V_MODEL.CI_DASHBOARD] : {
    ROW_1 : {
      x : 1010,
      y : 475
    },
  },
  [CONSTANT_V_MODEL.SOFTWARE_UNIT_TEST] : {
    ROW_1 : {
      x : 1010,
      y : 580
    },
    ROW_2 : {
      x : 1010,
      y : 535
    },
    ROW_3 : {
      x : 1010,
      y : 570
    }
  },
  [CONSTANT_V_MODEL.SOFTWARE_CONSTRUCTION] : {
    ROW_1 : {
      x : 1010,
      y : 675
    },
    // Y + 27
    ROW_2 : {
      x : 1010,
      y : 105
    }
  },
  // MANAGEMENT/SUPPORT TOOLS
  [CONSTANT_V_MODEL.PRJ_CONFIG_MANAGEMENT] : {
    ROW_1 : {
      x : 520,
      y : 790
    }
  },
  [CONSTANT_V_MODEL.PRJ_PLAN_CREATION_DELI] : {
    ROW_1 : {
      x : 520,
      y : 865
    }
  },
  [CONSTANT_V_MODEL.SW_CONFIG_MANAGEMENT] : {
    ROW_1 : {
      x : 520,
      y : 940
    }
  },
  [CONSTANT_V_MODEL.CHANGE_REQUEST] : {
    ROW_1 : {
      x : 520,
      y : 1015
    }
  }
}
