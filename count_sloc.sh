find -name '*.java' -o -name '*.kt' | xargs cat | grep -cvE ^\s*$
