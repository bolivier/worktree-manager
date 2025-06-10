function wtm
  if test $argv[1] = 'switch'
    set options (wtm list)
    set selected (printf "%s\n" $options | fzf)
    if test -n "$selected"
      cd "$selected"
    end
  else
    command wtm $argv
  end
end
