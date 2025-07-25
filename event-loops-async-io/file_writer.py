### Utility functions to write to file

def write_to_file(filename, data):
    with open(filename, "a", encoding="utf-8") as f:
        f.write(data)