$registerClientResourceType({
    "short_name": "sample_resource_type",
    "description": "sample_description",
    "priority": "1",
    "icon_class": "my_icon",
    "accept_regex":".*(md)"
});

$registerEditor({
    "id": "sample editor",
    "priority":"1",
    "type": "editor",
    "templateUrl": "editor.html",
    resources_type : [ "sample_resource_type","any"],
    "on_concurrent_update":function(){
        alert('on_concurrent_update callback')
        $vfs_readAllString(document.getElementById('filename').innerHTML, function(a) {
            document.getElementById('editor').value= a;
        });
    },
    "on_startup": function (uri) {
        $vfs_readAllString(uri, function(a) {
            alert('sample on_startup callback')
        });
    },
    "on_open":function(uri){
        $vfs_readAllString(uri, function(a) {
            document.getElementById('editor').value=a;
        });
        document.getElementById('filename').innerHTML = uri;
    }
});
