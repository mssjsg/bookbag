package github.io.mssjsg.bookbag.util.extension

fun String.encodeForFirebaseKey(): String {
    return replace("_", "__")
            .replace(".", "_P")
            .replace("$", "_D")
            .replace("#", "_H")
            .replace("[", "_O")
            .replace("]", "_C")
            .replace("/", "_S")
}

fun String.decodeForFirebaseKey(): String {
    var i = 0;
    var ni: Int;
    var res = "";
    ni = indexOf("_", i)
    while (ni != -1) {
        res += substring(i, ni);
        if (ni + 1 < length) {
            var nc = get(ni + 1);
            if (nc == '_') {
                res += '_';
            } else if (nc == 'P') {
                res += '.';
            } else if (nc == 'D') {
                res += '$';
            } else if (nc == 'H') {
                res += '#';
            } else if (nc == 'O') {
                res += '[';
            } else if (nc == 'C') {
                res += ']';
            } else if (nc == 'S') {
                res += '/';
            } else {
                // this case is due to bad encoding
            }
            i = ni + 2;
        } else {
            // this case is due to bad encoding
            break;
        }
        ni = indexOf("_", i)
    }
    res += substring(i);
    return res;
}