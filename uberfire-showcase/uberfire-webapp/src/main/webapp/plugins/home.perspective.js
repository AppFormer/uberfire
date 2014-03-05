$registerPerspective({
    id: "Home",
    is_default: true,
    panel_type: "root_list",
    view: {
        parts: [
            {
                place: "welcome",
                min_height: 100,
                parameters: {}
            }
        ],
        panels: [
            {
                width: 250,
                min_width: 200,
                position: "west",
                panel_type: "multi_list",
                parts: [
                    {
                        place: "YouTubeVideos",
                        parameters: {}
                    }
                ]
            },
            {
                width: 500,
                min_width: 500,
                position: "east",
                panel_type: "multi_list",
                parts: [
                    {
                        place: "PropertyEditorScreen",
                        parameters: {}
                    }
                ]
            },
            {
                height: 300,
                position: "south",
                panel_type: "multi_tab",
                parts: [
                    {
                        place: "YouTubeScreen",
                        parameters: {}
                    }
                ]
            }
        ]
    }
});
