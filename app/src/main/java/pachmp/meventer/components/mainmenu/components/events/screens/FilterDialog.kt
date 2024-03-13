package pachmp.meventer.components.mainmenu.components.events.screens

// @Destination(style = )
// @Composable
// fun FilterDialog(visible: MutableState<Boolean>) {
// var showAgeRange by mutableStateOf(false)
// var minAge by mutableStateOf("")
// var maxAge by mutableStateOf("")
//
// val animateShowRange by animateDpAsState(
// targetValue = if (showAgeRange) 100.dp else 0.dp,
// animationSpec = tween(durationMillis = 500, easing = LinearEasing), label = ""
// )
//
// /*var showTimeRange by mutableStateOf(false)
// var minTime by mutableStateOf("")
// var maxTime by mutableStateOf("")
//
// var showPriceRange by mutableStateOf(false)
// var minPrice by mutableStateOf("")
// var maxPrice by mutableStateOf("")
//
// var privateEvent by mutableStateOf(false)*/
//
// if (visible.value) {
// Dialog(onDismissRequest = { visible.value = false }) {
// Column {
// Text(text="Возрастной диапазон")
// Row {
// Switch(checked = showAgeRange, onCheckedChange = {showAgeRange=showAgeRange.not()})
// Text("от")
// OutlinedTextField(value = minAge, onValueChange = {minAge = it}, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
// Row(modifier = Modifier.widthIn(max=animateShowRange)) {
// Text("до")
// OutlinedTextField(value = maxAge, onValueChange = {maxAge = it}, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
// }
// }
// Text(text="Временной диапазон")
// Text(text="Ценовой диапазон")
// Text(text = "Приватное")
// }
//
// /*val id: Int,
// val name: String,
// val images: List<String>,
// val description: String,
// val startTime: Instant,
// val minimalAge: Short,
// val maximalAge: Short?,
// val price: Int,
// val participants: List<Int>,
// val originator: Int,
// val organizers: List<Int>,
// val favourite: Boolean*/
// }
// }
// }