#!/usr/bin/env python3

#
# This script converts a YAML file to a JSON file.
#
# More information at:
# https://github.com/julianmendez/tabulas
#

import sys, json, yaml

def main(argv):
    if (len(argv) == 3):
        input_file = argv[1]
        output_file = argv[2]
        with open(input_file, 'r') as stream:
            try:
                data = yaml.load(stream)
                with open(output_file, 'w') as output_file:
                    json.dump(data, output_file, indent=1)
            except yaml.YAMLError as exception:
                print(exception)
    else:
        print("usage: python3 " + argv[0] + " <YAML input file> <JSON output file>")


if __name__ == "__main__":
    main(sys.argv)


