#!/usr/bin/env bash

print_help() {
  echo "$0 <package owner> <new package name> <project name> <modid>"
}

if [ "$#" -ne "4" ]; then
  print_help
  exit 1
fi

base=$(dirname "$(readlink -f "$0")")
project_owner="$1"
package_name="$2"
project_name="$3"
modid="$4"
package_dir=$(echo "$package_name" | tr . /)

echo "Updating $base"
echo "Setting project owner to $project_owner"
echo "Setting package name to $package_name"
echo "Setting project name to $project_name"
echo "Setting mod id to $modid"
echo "Setting package dir to $package_dir"

(
  set -x

  # Update gradle.properties
  sed -i \
    -e "s/group=me.nobaboy/group=me.$project_owner/" \
    -e "s/mod.id=fabricmodtemplate/mod.id=$modid/" \
    -e "s/mod.name=FabricModTemplate/mod.name=$project_name/" \
    "$base"/gradle.properties

  # Update settings.gradle.kts
  sed -i "s/rootProject.name = \"FabricModTemplate\"/rootProject.name = \"$modid\"/" "$base"/settings.gradle.kts

  # Update main .kt file
  sed -i \
    -e "s/fabricmodtemplate/$modid/g" \
    -e "s/FabricModTemplate/$project_name/g" \
    -e "s/nobaboy/$project_owner/g" \
    "$base"/src/main/kotlin/me/nobaboy/fabricmodtemplate/FabricModTemplate.kt
  mv "$base"/src/main/kotlin/me/nobaboy/fabricmodtemplate/FabricModTemplate.kt "$base"/src/main/kotlin/me/nobaboy/fabricmodtemplate/"$project_name.kt"

  # Update the mixin file
  sed -i \
    -e "s/fabricmodtemplate/$modid/g" \
    -e "s/nobaboy/$project_owner/g" \
    "$base"/src/main/java/me/nobaboy/fabricmodtemplate/mixins/ClientMixin.java

  # Rename directories
  # Kotlin dir
  mkdir -p "$base"/src/main/kotlin/"$package_dir"
  mv "$base"/src/main/kotlin/me/nobaboy/fabricmodtemplate/* "$base"/src/main/kotlin/"$package_dir"
  rm -r "$base"/src/main/kotlin/me/nobaboy/fabricmodtemplate
  # Java dir
  mkdir -p "$base"/src/main/java/"$package_dir"
  mv "$base"/src/main/java/me/nobaboy/fabricmodtemplate/* "$base"/src/main/java/"$package_dir"
  rm -r "$base"/src/main/java/me/nobaboy/fabricmodtemplate

  # Update fabric.mod.json
  sed -i \
    -e "s/fabricmodtemplate/$modid/g" \
    -e "s/FabricModTemplate/$project_name/g" \
    -e "s/nobaboy/$project_owner/g" \
    "$base"/src/main/resources/fabric.mod.json

  # Update and rename fabricmodtemplate.mixins.json
  sed -i \
    -e "s/fabricmodtemplate/$modid/g" \
    -e "s/nobaboy/$project_owner/g" \
    "$base"/src/main/resources/fabricmodtemplate.mixins.json
  mv "$base"/src/main/resources/fabricmodtemplate.mixins.json "$base"/src/main/resources/"$modid".mixins.json

  # Remove the init workflow script
  rm "$base"/.github/workflows/init.yml
  rm "$(readlink -f $0)"
)
echo "All done, go commit these changes!"