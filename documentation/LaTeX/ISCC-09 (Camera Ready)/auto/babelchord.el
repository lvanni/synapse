(TeX-add-style-hook "babelchord"
 (lambda ()
    (LaTeX-add-environments
     "theorem"
     "prop"
     "lemma"
     "definition"
     "example"
     "remark")
    (LaTeX-add-bibitems
     "CCL08"
     "Datta"
     "Biersack"
     "Biersack2"
     "LC07b"
     "CAN"
     "Haridi"
     "Chord"
     "brocade")
    (LaTeX-add-labels
     "fig:multiFloorExample"
     "fig:lookup"
     "fig:join"
     "fig:business"
     "ssec:lookup"
     "ssec:tower"
     "fig:bab_last"
     "fig:simu1")
    (TeX-add-symbols
     '("up" 1)
     '("down" 1)
     '("fwd" 1)
     '("rew" 1)
     '("verbi" 1)
     "ie"
     "eg"
     "Ce"
     "Fr"
     "Lu"
     "RESP")
    (TeX-run-style-hooks
     "graphics"
     "color"
     "usenames"
     "alltt"
     "fullpage"
     "url"
     "algorithm"
     "boxedminipage"
     "float"
     "amssymb"
     "epsfig"
     "comment"
     "latex8"
     "inputenc"
     "latin1"
     "latex2e"
     "art10"
     "article"
     "times"
     "10pt"
     "twocolumn"
     "algorithmic_header")))

