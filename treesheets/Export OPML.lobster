// This example script exports a TreeSheet to OPML.


include "std.lobster"

out := []

def add_text(s):
    out.push("\"")
    // escape \ and " 
    out.push(escapestring(s, "\\\"", "\\", ""))
    out.push("\"")

def add_indent(indent):
    out.push(concatstring(map(indent): "  ", ""))

def add_cell(indent):
    add_indent(indent + 1)
    out.push("<outline text=")
    add_text(ts_get_text())
    if ts_num_children():
        out.push(">\n")
        size := ts_num_columns_rows()
        for(ts_num_children()) i:
            ts_goto_child(i)
            if ts_num_children():
                add_cell(indent + 1)
            else:
                add_indent(indent + 2)
                out.push("<outline text=")
                add_text(ts_get_text())
                out.push(" />\n")
            ts_goto_parent()

        add_indent(indent + 1)
        out.push("</outline>\n")    
    else:
        out.push("/>\n")    
        

fn := ts_get_filename_from_user(true)
if fn.length:
    ts_goto_root()
    out.push("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
    out.push("<opml version=\"1.1\">\n")
    out.push("<head>\n")
    //TODO: ...
    out.push("</head>\n")
    out.push("<body>\n")
    add_cell(0)
    out.push("</body>\n")
    out.push("</opml>\n")
    ok := write_file(fn, concatstring(out, ""))
    ts_set_status_message("OPML export " + fn + " - " + if ok: "succesful" else: "failed!")
else:
    ts_set_status_message("export cancelled")
