//
// See:
// https://github.com/aardappel/treesheets/blob/master/TS/scripts/Export%20JSON.lobster
// This example script exports a TreeSheet to HTML.


include "std.lobster"

out := []

def add_text(s):
    //out.push("\"")
    // \ and " need to be escaped in JSON:
    //out.push(escapestring(s, "\\\"", "\\", ""))
    out.push(s)
    //out.push("\"")

def add_indent(indent):
    out.push(concatstring(map(indent): "  ", ""))

def add_cell(indent):
    //out.push("<td>\n")
    add_indent(indent + 1)
    add_text(ts_get_text())
    if ts_num_children():
        out.push("<table>")
        size := ts_num_columns_rows()
        for(size.y) i:
            out.push("<tr>")
            for(size.x) j:
                ts_goto_column_row(j,i)
                add_indent(indent + 1)
                out.push("<td>")
                if ts_num_children():
                    add_cell(indent + 1)
                else:
                    add_text(ts_get_text())
                out.push("</td>")
                ts_goto_parent()
            out.push("</tr>")

        out.push("</table>\n")    
    add_indent(indent)
    //out.push("</td>")

fn := ts_get_filename_from_user(true)
if fn.length:
    ts_goto_root()  // This the default, here just for clarity.
    out.push("<html>\n")
    out.push("<body>\n")
    out.push("<head><style>\n")
    out.push("table {  border-collapse: collapse; }\n")
    out.push(" table, th, td {\n")
    out.push("    border: 1px solid black;\n")
    out.push("}</style></head>\n")
    out.push("</body>\n")
    add_cell(0)
    out.push("</body>\n")
    out.push("</html>\n")
    ok := write_file(fn, concatstring(out, ""))
    ts_set_status_message("JSON export " + fn + " - " + if ok: "succesful" else: "failed!")
else:
    ts_set_status_message("export cancelled")
