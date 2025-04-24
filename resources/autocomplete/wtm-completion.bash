 #!/usr/bin/env bash

_wtm_completions() {
  local cur="${COMP_WORDS[COMP_CWORD]}"
  local completions
  completions=$(wtm _complete "${COMP_WORDS[@]:1:$COMP_CWORD-1}" 2>/dev/null)
  COMPREPLY=( $(compgen -W "${completions}" -- "$cur") )
}
complete -F _wtm_completions wtm
