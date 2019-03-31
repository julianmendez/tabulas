#!/usr/bin/env python3

#
# This script converts a YAML file to a JSON file.
#
# More information at:
# https://github.com/julianmendez/tabulas
#

import json
import sys
import yaml


def main(argv):
    if (len(argv) == 3):
        input_file_name = argv[1]
        output_file_name = argv[2]
        with open(input_file_name, 'r') as input_file:
            try:
                data = yaml.load(input_file)
                with open(output_file_name, 'w') as output_file:
                    json.dump(data, output_file, indent=1)
            except yaml.YAMLError as exception:
                print(exception)
    else:
        print("usage: python3 " + argv[0] + " <YAML input file> <JSON output file>")


if __name__ == "__main__":
    main(sys.argv)


