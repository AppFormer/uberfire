$registerResourceType({
    id: "FeatureResourceType",
    short_name: "Feature Resource Type",
    description: "Feature Description",
    prefix: "",
    suffix: "feature",
    priority: "1000",
    simple_wildcard_pattern: "*.feature",
    "accept": function (filename) {
        var extension = filename.split('.').pop();
        if (extension === "feature")
        { return true; }
        return false;
    }
});