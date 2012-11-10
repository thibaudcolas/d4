console.log 'D4 launched ! ' + projectURI

oneDay = 24*60*60*1000

timeDisplay = (days) ->
	if pageLang is 'fr' then "Il y a #{days} jours"
	else "#{days} days ago"

diffDays = (date) ->
	now = new Date().getTime()
	thenn = new Date(date).getTime()
	Math.round Math.abs (now - thenn) / oneDay

$ ->

	$('time').tooltip {title: -> timeDisplay diffDays $(@).html()}

	$('.tooltip-js').tooltip()

	$('dd').popover {trigger: 'hover', placement: 'right'}

	$('.triples').popover {html: on, trigger: 'hover', placement: 'bottom', title: "#{extractLabel}", content: -> extract[$(@).attr 'data-index']}

	$('.long-string').each ->
		$this = $(@)
		if $this.html().length > 35
			$this.tooltip {title: $this.html()}
			$this.html _.prune $this.html(), 35, '&hellip;'

	$('.dimension-string').each ->
		$this = $(@)
		$this.tooltip()
		if _.startsWith $this.html(), 'http://'
			$this.html _.humanize _.strRightBack $this.html(), '/'
		else
			$this.html _.titleize _.swapCase $this.html()
		return

	$('.measure-int').each ->
		$this = $(@)
		$this.html _.numberFormat (_.toNumber $this.html()), 0, decimalSeparator, thousandsSeparator

	# $('.observation-barchart').each ->
	# 	$this = $(@)
	# 	index = $this.attr 'data-index'
	# 	dd = [4, 8, 15, 16, 23, 42];
	# 	chart = d3.select("#innerchart" + index).append("div").attr "class", "chart"
	# 	chart.selectAll("div").data(dd).enter().append("div").style("width", (d) -> d * 10 + "px").text (d) -> d
	# 	return

	if pageLang is 'en'
		$('.observation-table').dataTable {
	    'aoColumns': [{ 'sType': 'html' }, { 'sType': 'data-numeric' }]
	  }
	else 
		$('.observation-table').dataTable {'aoColumns': [{ 'sType': 'html' }, { 'sType': 'data-numeric' }], 'oLanguage': {'sProcessing':     'Traitement en cours...','sSearch':         'Rechercher&nbsp;:','sLengthMenu':     'Afficher _MENU_ &eacute;l&eacute;ments','sInfo':           'Affichage de l\'&eacute;lement _START_ &agrave; _END_ sur _TOTAL_ &eacute;l&eacute;ments','sInfoEmpty':      'Affichage de l\'&eacute;lement 0 &agrave; 0 sur 0 &eacute;l&eacute;ments','sInfoFiltered':   '(filtr&eacute; de _MAX_ &eacute;l&eacute;ments au total)','sInfoPostFix':    '','sLoadingRecords': 'Chargement en cours...','sZeroRecords':    'Aucun &eacute;l&eacute;ment &agrave; afficher','sEmptyTable':     'Aucune donnée disponible dans le tableau','oPaginate': {'sFirst':      'Premier', 'sPrevious':   'Pr&eacute;c&eacute;dent', 'sNext':       'Suivant', 'sLast':       'Dernier'},'oAria': {'sSortAscending':  ': activer pour trier la colonne par ordre croissant', 'sSortDescending': ': activer pour trier la colonne par ordre décroissant'}}}
		console.log '?'

	console.log 'This is awesome !'
