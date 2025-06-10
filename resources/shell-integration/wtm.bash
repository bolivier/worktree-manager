wtm() {
  if [ "$1" = "switch" ]; then
    # Capture output of `wtm list` into an array
    IFS=$'\n' read -r -d '' -a options < <(wtm list; printf '\0')

    # Present options with fzf
    selected=$(printf "%s\n" "${options[@]}" | fzf)

    # If the user selected a directory, cd into it
    if [ -n "$selected" ]; then
      cd "$selected" || return
    fi
  else
    command wtm "$@"
  fi
}
