package com.example.profilecardlayout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberAsyncImagePainter
import com.example.profilecardlayout.ui.theme.MyTheme
import com.example.profilecardlayout.ui.theme.lightGreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                UserApplication()
            }
        }
    }
}

@Composable
fun UserApplication(userProfiles: List<UserProfile> = userProfileList) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "user-list") {
        composable("user-list") {
            ProfileListScreen(userProfiles, navController)
        }
        composable(route = "user-details/{userId}", arguments = listOf(navArgument("userId") {
            type = NavType.IntType
        })) { navBackStackEntry ->
            ProfileDetailsScreen(navBackStackEntry.arguments!!.getInt("userId"), navController)
        }
    }
}

@Composable
fun ProfileListScreen(
    userProfiles: List<UserProfile> = userProfileList,
    navHostController: NavHostController?
) {
    Scaffold(topBar = { AppBar(title = "Users list", icon = Icons.Default.Home) {} }) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn {
                items(userProfiles) { userProfile ->
                    ProfileCard(userProfile = userProfile) {
                        //trailing lambda
                        navHostController?.navigate("user-details/${userProfile.id}")

                    }
                }
            }
        }
    }
}

@Composable
fun AppBar(title: String, icon: ImageVector, iconClickAction: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            Icon(
                imageVector = icon,
                "content description",
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { iconClickAction.invoke() },
            )
        },
        title = { Text(title) })
}

@Composable
fun ProfileCard(userProfile: UserProfile, clickAction: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .clickable { clickAction.invoke() },
        elevation = 8.dp,
        backgroundColor = androidx.compose.ui.graphics.Color.White,

        ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            ProfilePicture(userProfile.pictureUrl, userProfile.status, 72.dp)
            ProfileContent(userProfile.name, userProfile.status, Alignment.Start)
        }
    }
}

@Composable
fun ProfilePicture(pictureUrl: String, onlineStatus: Boolean, imageSize: Dp) {
    Card(
        shape = CircleShape,
        border = BorderStroke(
            width = 2.dp,
            color = if (onlineStatus) MaterialTheme.colors.lightGreen else androidx.compose.ui.graphics.Color.Red
        ),
        modifier = Modifier.padding(16.dp),
        elevation = 4.dp
    ) {
        Image(
            painter = rememberAsyncImagePainter(pictureUrl),
            contentDescription = "Profile picture",
            modifier = Modifier.size(imageSize),
            contentScale = ContentScale.Crop
        )
    }

}

@Composable
fun ProfileContent(name: String, onlineStatus: Boolean, alignment: Alignment.Horizontal) {
    Column(
        modifier = Modifier
            .padding(8.dp),
        horizontalAlignment = alignment
    ) {
        Text(text = name, style = MaterialTheme.typography.h5)
        Text(
            text = if (onlineStatus) "Active now" else "Offline",
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
fun ProfileDetailsScreen(userId: Int, navHostController: NavHostController?) {
    val userProfile = userProfileList.first { userProfile ->
        userId == userProfile.id
    }
    Scaffold(topBar = {
        AppBar(title = "User profile details", icon = Icons.Default.ArrowBack) {
            navHostController?.navigateUp()
        }
    }) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                ProfilePicture(userProfile.pictureUrl, userProfile.status, 240.dp)
                ProfileContent(
                    userProfile.name,
                    userProfile.status,
                    Alignment.CenterHorizontally
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfilePreview() {
    MyTheme {
        ProfileDetailsScreen(userId = 0, null)
    }
}

@Preview(showBackground = true)
@Composable
fun UserListPreview() {
    MyTheme {
        ProfileListScreen(userProfiles = userProfileList, null)
    }
}