#compdef wtm

_wtm() {
  local completions
  completions=("${(@f)$(wtm _complete ${words[@]:1})}")
  _wanted values expl 'wtm completions' compadd -a completions
}

compdef _wtm wtm
