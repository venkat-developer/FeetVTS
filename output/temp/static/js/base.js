/**
 * A method all widgets should use to package the Javascript Widgets.
 * A general usage will be getPackageForName("com.i10n.fleet.widget.ui")
 *
 * @param {Object} packageName
 */
function getPackageForName(packageName){
    var arr = packageName.split('.');
    
    var obj = window;
    for (var i = 0; i < arr.length; i++) {
        if (typeof obj[arr[i]] === 'undefined') {
        
            obj[arr[i]] = {};
        }
        
        obj = obj[arr[i]];
    }
    
    return obj;
}

_instances = {};
