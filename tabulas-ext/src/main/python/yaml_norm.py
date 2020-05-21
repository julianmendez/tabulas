#!/usr/bin/env python3

#
# This script normalizes a YAML file.
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
                data = yaml.safe_load(input_file)
                with open(output_file_name, 'w') as output_file:
                    yaml.safe_dump(data, output_file, default_flow_style=False, sort_keys=False, explicit_start=True)
            except yaml.YAMLError as exception:
                print(exception)
    else:
        print("usage: python3 " + argv[0] + " <YAML input file> <YAML output file>")


if __name__ == "__main__":
    main(sys.argv)


