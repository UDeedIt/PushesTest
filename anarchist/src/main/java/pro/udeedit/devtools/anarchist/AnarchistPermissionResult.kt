package pro.udeedit.devtools.anarchist

class AnarchistPermissionResult {
    var permissionStatus: HashMap<String, AnarchistPermissionStatus> = hashMapOf()
    var finalStatus: AnarchistPermissionStatus = AnarchistPermissionStatus.DENIED
}