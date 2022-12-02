#!/bin/bash


ORIGIN_POM="$1"
TEMPLATE_POM="$2"
DESTINATION_PRJ_DIR="$3"
DESTINATION_POM="$DESTINATION_PRJ_DIR/scan-prj/pom.xml"

#~

INDEP=false
INEXC=false
DEPS=""

while IFS= read -r ln; do

  [[ "$ln" == *"</dependencies>"* ]] && INDEP=false
  [[ "$ln" == *"<exclusions>"* ]] && INEXC=true

  $INDEP && ! $INEXC && DEPS+="${ln:4}"$'\n'

  [[ "$ln" == *"</exclusions>"* ]] && INEXC=false
  [[ "$ln" == *"<dependencies>"* ]] && INDEP=true

done < "$ORIGIN_POM"

#~

DF="$DESTINATION_POM"

rm "$DF"
touch "$DF"

while IFS= read -r ln; do

  if [[ "$ln" == *"<dependencies>"* ]]; then
    echo "$ln" >> "$DF"
    echo "$DEPS" >> "$DF"
  else
    echo "$ln" >> "$DF"
  fi

done < "$TEMPLATE_POM"

cp "$DESTINATION_PRJ_DIR/.snyk" "$DESTINATION_PRJ_DIR/scan-prj/"

