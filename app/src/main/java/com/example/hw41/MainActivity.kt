package com.example.hw41

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.hw41.ui.theme.HW41Theme
import org.xmlpull.v1.XmlPullParser

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val contxt = LocalContext.current
            val parser = contxt.resources.getXml(R.xml.products)
            var type = parser.eventType
            var productList: MutableList<Prod> = mutableListOf()
            while (type != XmlPullParser.END_DOCUMENT) {
                if (type == XmlPullParser.START_TAG && parser.name == "Product") {
                    var name = parser.getAttributeValue(null, "name")
                    var picture = contxt.resources.getIdentifier(parser.getAttributeValue(null, "picture"), "drawable", contxt.packageName)
                    var description = parser.getAttributeValue(null, "description")
                    var price = parser.getAttributeValue(null, "price")
                    var extra: List<Int> =
                        parser.getAttributeValue(null, "extra").split(" ").map { each ->
                            contxt.resources.getIdentifier(each, "drawable", contxt.packageName)
                        }
                    productList.add(Prod(name = name, picture = picture, description = description, extra = extra, price = price))
                }
                type = parser.next()
            }
            HW41Theme {
                shoppingList(productList)
            }
        }
    }
}

data class Prod(
    val name: String?,
    val picture: Int?,
    val price: String?,
    val description: String?,
    val extra: List<Int>?,
)

@Composable
fun productInfo(
    product: Prod,
    callBack: (Int) -> Unit,
    idx: Int,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth(
                    0.9f,
                ).height(
                    120.dp,
                ).padding(vertical = 2.dp)
                .zIndex(1f)
                .border(
                    width = 5.dp,
                    brush = Brush.radialGradient(radius = 10f, colors = listOf(Color(0xFFbf8636), Color(0xFFde8810))),
                    shape = RoundedCornerShape(20.dp),
                ).clip(shape = RoundedCornerShape(20.dp))
                .clickable {
                    callBack(idx)
                },
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = product.picture!!),
            contentDescription = product.name!!,
            contentScale = ContentScale.Fit,
            modifier = Modifier.width(70.dp).height(70.dp),
        )
        Spacer(modifier = Modifier.defaultMinSize(minWidth = 5.dp))
        Column(Modifier.fillMaxWidth(0.8f).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                modifier = Modifier.padding(6.dp),
                maxLines = 3,
                text = product.name!!,
                fontFamily = FontFamily(Font(R.font.hanzipen)),
                softWrap = true,
                textAlign = TextAlign.Center,
                color = Color(0xFF040f26),
                fontSize = 15.sp,
                fontWeight = FontWeight.W200,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = product.price!!, fontSize = 20.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun shoppingList(products: List<Prod>) {
    var call by remember { mutableIntStateOf(-1) }
    val info = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
    val scrollState = rememberScrollState()
    var imageMag by remember { mutableStateOf<Int?>(null) }
    when (info) {
        WindowWidthSizeClass.COMPACT -> {
            Box(
                modifier = Modifier.fillMaxSize().background(if (call != -1) Color.DarkGray else Color.White),
                contentAlignment = Alignment.Center,
            ) {
                if (call != -1) {
                    if (imageMag != null) {
                        Box(modifier = Modifier.fillMaxSize().clickable { imageMag = null }, contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = imageMag!!),
                                contentScale = ContentScale.Fit,
                                contentDescription = "Mag",
                                modifier =
                                    Modifier.fillMaxSize(0.8f).pointerInput(Unit) {
                                        awaitPointerEventScope {
                                            val event =
                                                awaitPointerEvent()
                                            event.changes.forEach { it.consume() }
                                        }
                                    },
                            )
                        }
                    } else {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 20.dp, vertical = 100.dp)
                                    .background(Color.DarkGray)
                                    .verticalScroll(scrollState)
                                    .clickable { call = -1 },
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = products.get(index = call).description!!,
                                softWrap = true,
                                letterSpacing = 1.sp,
                                textAlign = TextAlign.Start,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.W300,
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                                products.get(index = call).extra!!.map { each ->
                                    (
                                        Image(
                                            modifier =
                                                Modifier.defaultMinSize(minWidth = 120.dp, minHeight = 120.dp).pointerInput(Unit) {
                                                    imageMag = each
                                                },
                                            painter = painterResource(id = each),
                                            contentScale = ContentScale.Fit,
                                            contentDescription = "pictures",
                                        )
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(1f).height(50.dp).padding(start = 10.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.shopping_cart),
                                contentDescription = "Shopping Cart",
                                modifier = Modifier.width(30.dp).height(30.dp),
                            )
                            Spacer(modifier = Modifier.defaultMinSize(minWidth = 10.dp))
                            Text(
                                text = "Cart",
                                color = Color.DarkGray,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.W600,
                            )
                        }
                        Spacer(modifier = Modifier.defaultMinSize(minHeight = 30.dp))
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            itemsIndexed(products) { idx, prod ->
                                productInfo(product = prod, idx = idx, callBack = { idx -> call = idx })
                            }
                        }
                    }
                }
            }
        }

        WindowWidthSizeClass.EXPANDED -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (imageMag != null) {
                    Box(modifier = Modifier.fillMaxSize().clickable { imageMag = null }, contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = imageMag!!),
                            contentScale = ContentScale.Fit,
                            contentDescription = "Mag",
                            modifier =
                                Modifier.fillMaxSize(0.8f).pointerInput(Unit) {
                                    awaitPointerEventScope {
                                        val event =
                                            awaitPointerEvent()
                                        event.changes.forEach { it.consume() }
                                    }
                                },
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxSize(0.9f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(0.3f).fillMaxHeight()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(1f).height(50.dp).padding(start = 10.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.shopping_cart),
                                    contentDescription = "Shopping Cart",
                                    modifier = Modifier.width(30.dp).height(30.dp),
                                )
                                Spacer(modifier = Modifier.defaultMinSize(minWidth = 10.dp))
                                Text(
                                    text = "Cart",
                                    color = Color.DarkGray,
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.W600,
                                )
                            }

                            LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                itemsIndexed(products) { idx, prod ->
                                    productInfo(product = prod, idx = idx, callBack = { idx -> call = idx })
                                }
                            }
                        }

                        if (call != -1) {
                            Column(
                                modifier =
                                    Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(0.7f)
                                        .padding(vertical = 20.dp)
                                        .verticalScroll(scrollState)
                                        .clickable { call = -1 },
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = products.get(index = call).description!!,
                                    softWrap = true,
                                    letterSpacing = 1.sp,
                                    textAlign = TextAlign.Start,
                                    color = Color.DarkGray,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.W300,
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                                    products.get(index = call).extra!!.map { each ->
                                        (
                                            Image(
                                                modifier =
                                                    Modifier.defaultMinSize(minWidth = 100.dp, minHeight = 100.dp).pointerInput(Unit) {
                                                        imageMag = each
                                                    },
                                                painter = painterResource(id = each),
                                                contentScale = ContentScale.Fit,
                                                contentDescription = "pictures",
                                            )
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.defaultMinSize(minWidth = 20.dp))
                        }
                    }
                }
            }
        }
        WindowWidthSizeClass.MEDIUM -> {
        }
    }
}

@Preview(showBackground = true)
@Composable
fun test() {
    val contxt = LocalContext.current
    val parser = contxt.resources.getXml(R.xml.products)
    var type = parser.eventType
    var productList: MutableList<Prod> = mutableListOf()
    while (type != XmlPullParser.END_DOCUMENT) {
        if (type == XmlPullParser.START_TAG && parser.name == "Product") {
            var name = parser.getAttributeValue(null, "name")
            var picture = contxt.resources.getIdentifier(parser.getAttributeValue(null, "picture"), "drawable", contxt.packageName)
            var description = parser.getAttributeValue(null, "description")
            var extra: List<Int> =
                parser.getAttributeValue(null, "extra").split(" ").map { each ->
                    contxt.resources.getIdentifier(each, "drawable", contxt.packageName)
                }
            productList.add(Prod(name = name, picture = picture, description = description, extra = extra, price = null))
        }
        type = parser.next()
    }
    HW41Theme {
        shoppingList(productList)
    }
}
