from pathlib import Path
import re
from collections import defaultdict
import os


# https://stackoverflow.com/a/31852401
def load_properties(filepath, sep='=', comment_char='#'):
	"""Read the file passed as parameter as a properties file."""
	props = {}
	with open(filepath, "rt") as f:
		for line in f:
			l = line.strip()
			if l and not l.startswith(comment_char):
				key_value = l.split(sep)
				key = key_value[0].strip()
				value = sep.join(key_value[1:]).strip().strip('"')
				props[key] = value
	return props


def current_mod_version() -> str:
	if override := os.environ.get("VERSION"):
		return override
	return load_properties(REPO_DIR / "gradle.properties")["mod.version"]


REPO_DIR = Path(__file__).parent.parent
RELEASE_HEADER = re.compile(r"## (?P<VERSION>[\d.\w-]+|Unreleased)(?: - [\d]{4}-[\d]{1,2}-[\d]{1,2})?")
releases = defaultdict(lambda: "")

with open(REPO_DIR / "CHANGELOG.md") as f:
	current = None
	for line in f.readlines():
		if match := RELEASE_HEADER.match(line):
			current = match.group("VERSION")
			continue
		if current is not None:
			releases[current] += line


HEADER = re.compile(r"^#(#+)", flags=re.MULTILINE)
ISSUE_LINK = re.compile(r"GH-(\d+)")

with open("CHANGELOG.mini", mode="w") as f:
	current = current_mod_version()
	notes = releases[current].strip()
	notes = HEADER.sub(r"\1", notes)
	notes = ISSUE_LINK.sub(r"[#\1](https://github.com/nobaboy/NobaAddons/issues/\1)", notes)
	f.write(notes)
