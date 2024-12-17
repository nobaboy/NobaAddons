#!/usr/bin/env python3

import requests
from pathlib import Path
import os
from typing import Iterator

CHANGELOG = ""
VERSION = ""
mc_versions = os.listdir("versions")

with open(Path(__file__).parent.parent / "gradle.properties") as f:
    for line in f.readlines():
        if line.startswith("mod.version="):
            VERSION = line.replace("mod.version=", "").strip()
            break
    else:
        raise RuntimeError("Couldn't find mod.version property!")

with open(Path(__file__).parent.parent / "CHANGELOG.mini") as f:
    CHANGELOG = f.read()

versions = "\n".join(
    f"- [{x}](<https://modrinth.com/mod/nobaaddons/version/{VERSION}+{x}>)"
    for x in mc_versions
)


# The following function is taken from Red and modified to remove behaviour we don't need
# https://github.com/Cog-Creators/Red-DiscordBot/blob/ad6b8662b2/redbot/core/utils/chat_formatting.py#L235-L305
def pagify(
    text: str,
    delims=None,
    *,
    priority: bool = False,
    shorten_by: int = 8,
    page_length: int = 2000,
) -> Iterator[str]:
    if delims is None:
        delims = ["\n"]

    in_text = text
    page_length -= shorten_by
    while len(in_text) > page_length:
        closest_delim = (in_text.rfind(d, 1, page_length) for d in delims)
        if priority:
            closest_delim = next((x for x in closest_delim if x > 0), -1)
        else:
            closest_delim = max(closest_delim)
        closest_delim = closest_delim if closest_delim != -1 else page_length
        to_send = in_text[:closest_delim]
        if len(to_send.strip()) > 0:
            yield to_send
        in_text = in_text[closest_delim:]

    if len(in_text.strip()) > 0:
        yield in_text


message = f"""
# `{VERSION}` has been released

<:modrinth:1040805511538421890> **Download from Modrinth:**
{versions}

# Changelog

{CHANGELOG}
""".strip()


if os.environ.get("DRY_RUN"):
    print(message)
    exit(0)


WEBHOOK = os.environ["DISCORD_WEBHOOK"]

for page in pagify(message):
    requests.post(
        f"{WEBHOOK}?wait=true",
        json={
            "content": page,
            "allowed_mentions": {"parse": []}
        },
        headers={"Content-Type": "application/json"},
    )
