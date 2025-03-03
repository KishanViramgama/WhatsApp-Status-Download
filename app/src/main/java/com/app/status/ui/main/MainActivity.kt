package com.app.status.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.IntentCompat
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.status.BuildConfig
import com.app.status.R
import com.app.status.base.BaseActivity
import com.app.status.datastore.MyDataStore.Companion.isWhatsAppAllow
import com.app.status.datastore.MyDataStore.Companion.isWhatsAppBusinessAllow
import com.app.status.datastore.MyDataStore.Companion.themSettingKey
import com.app.status.ui.home.download.Download
import com.app.status.ui.home.download.DownloadViewModel
import com.app.status.ui.home.image.Image
import com.app.status.ui.home.image.ImageViewModel
import com.app.status.ui.home.item.DrawerItem
import com.app.status.ui.home.video.Video
import com.app.status.ui.home.video.VideoViewModel
import com.app.status.ui.main.widget.NavHeader
import com.app.status.ui.setting.Setting
import com.app.status.ui.theme.StatusThem
import com.app.status.util.Const.isDark
import com.app.status.util.Const.whatsApp11
import com.app.status.util.Const.whatsAppBusiness11
import com.app.status.util.Const.whatsAppBussinessPackageName
import com.app.status.util.Const.whatsAppPackageName
import com.app.status.util.FontEnum
import com.app.status.util.ShowMyDialog
import com.app.status.util.convertPathToContentUri
import com.app.status.util.encodedUrl
import com.app.status.util.getDownloadPath
import com.app.status.util.isAppInstalled
import com.app.status.util.isPermission
import com.app.status.util.themValue
import com.app.status.widget.MyAdView
import com.app.status.widget.MyText
import com.app.status.widget.NoDataFound
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Objects


@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private var isDarkMode by mutableStateOf(false)

    //Show dialog permission message
    private var isShowDialog by mutableStateOf(false)

    //Show dialog setting
    private var isShowDialogSetting by mutableStateOf(false)

    //Show dialog folder access
    private var isShowDialogFolder by mutableStateOf(false)

    //Show dialog application not installed
    private var isApplicationInstalled by mutableStateOf(true)

    //Show data
    private var isShowData by mutableStateOf(false)

    //Status
    private var title by mutableStateOf("")

    private var isBothAppInstall: Boolean = false

    private lateinit var itemsDrawer: List<DrawerItem>

    private lateinit var listTab: List<String>

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this) {
            finish()
        }

        listTab = listOf(
            resources.getString(R.string.image),
            resources.getString(R.string.video),
            resources.getString(R.string.download)
        )

        itemsDrawer = arrayListOf(
            DrawerItem(resources.getString(R.string.home), R.drawable.ic_home),
            DrawerItem(resources.getString(R.string.setting), R.drawable.ic_settings)
        )

        CoroutineScope(Dispatchers.IO).launch {
            val themType = myDataStore.getMyDataStoreString(themSettingKey, "0").first()
            isDark = themValue(themType)
            isDarkMode = isDark
        }

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val uri = Objects.requireNonNull<Intent>(result.data).data
                    try {
                        contentResolver.takePersistableUriPermission(
                            uri!!, Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )

                        CoroutineScope(Dispatchers.IO).launch {

                            withContext(Dispatchers.IO) {
                                if (uri.toString().contains(convertPathToContentUri(whatsApp11).toString())) {
                                    myDataStore.setMyDataStoreBoolean(isWhatsAppAllow, true)
                                }
                            }

                            withContext(Dispatchers.IO) {
                                if (uri.toString().contains(convertPathToContentUri(whatsAppBusiness11).toString())) {
                                    myDataStore.setMyDataStoreBoolean(isWhatsAppBusinessAllow, true)
                                }
                            }

                            withContext(Dispatchers.IO){
                                if (isBothAppInstall) {
                                    if (!myDataStore.getMyDataStoreBoolean(
                                            isWhatsAppAllow, defaultValue = false
                                        ).first()
                                    ) {
                                        openFolder(whatsApp11)
                                    } else if (!myDataStore.getMyDataStoreBoolean(
                                            isWhatsAppBusinessAllow, defaultValue = true
                                        ).first()
                                    ) {
                                        openFolder(whatsAppBusiness11)
                                    } else {
                                        isShowDialogFolder = false
                                        isShowData = true
                                    }

                                } else {
                                    isShowDialogFolder = false
                                    isShowData = true
                                }
                            }




                        }
                        Log.d("data_path", uri.toString())
                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this, "Not Success $e", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Not Success", Toast.LENGTH_SHORT).show()
                }
            }

        /**
        Single permission check
         */
        val launcherPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                        openFolderDialog()
                    } else {
                        isShowData = true
                    }
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        isShowDialog = true
                    } else {
                        isShowDialogSetting = true
                    }
                }
            }

        val requestSettingPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _: ActivityResult ->
                val isPermission = isPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                if (isPermission) {
                    isShowData = true
                } else {
                    isShowDialogSetting = true
                }
            }

        isBothAppInstall =
            isAppInstalled(whatsAppPackageName) && isAppInstalled(whatsAppBussinessPackageName)

        val isAppInstalled =
            isAppInstalled(whatsAppPackageName) || isAppInstalled(whatsAppBussinessPackageName)
        isApplicationInstalled = isAppInstalled


        if (isAppInstalled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                openFolderDialog()
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                openFolderDialog()
            } else {
                launcherPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        setContent {
            StatusThem(isDarkMode) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

                    val scope = rememberCoroutineScope()
                    val drawerState = rememberDrawerState(DrawerValue.Closed)

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        gesturesEnabled = true,
                        content = {
                            Scaffold(topBar = {
                                TopAppBar(
                                    title = {
                                        MyText(
                                            text = title,
                                            fontSize = 18.sp,
                                            fontFamily = FontEnum.BOlD
                                        )
                                    },
                                    navigationIcon = {
                                        IconButton(onClick = {
                                            scope.launch {
                                                if (drawerState.isOpen) {
                                                    drawerState.close()
                                                } else {
                                                    drawerState.open()
                                                }
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Rounded.Menu,
                                                contentDescription = "Drawer Icon"
                                            )
                                        }
                                    },
                                    colors = TopAppBarDefaults.topAppBarColors()
                                )
                            }, content = {
                                if (isShowData) {
                                    DrawerContain(
                                        modifier = Modifier
                                            .padding(it), selectedItem
                                    )
                                }
                            }, bottomBar = {
                                MyAdView()
                            })
                        },
                        drawerContent = {
                            ModalDrawerSheet(
                                modifier = Modifier.wrapContentWidth(),
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxHeight()
                                ) {
                                    NavHeader()
                                    itemsDrawer.forEachIndexed { index, item ->
                                        NavigationDrawerItem(
                                            icon = {
                                                Icon(
                                                    painter = painterResource(id = item.icon),
                                                    contentDescription = resources.getString(R.string.app_name),
                                                    modifier = Modifier
                                                        .width(32.dp)
                                                        .height(32.dp)
                                                )
                                            },
                                            label = { MyText(text = item.name) },
                                            selected = index == selectedItem,
                                            onClick = {
                                                scope.launch { drawerState.close() }
                                                selectedItem = index
                                            },
                                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                        )
                                    }
                                }
                            }
                        },
                    )

                    if (isShowDialog) {
                        ShowMyDialog(
                            title = stringResource(R.string.permission),
                            msg = stringResource(R.string.permission_msg),
                            positiveText = stringResource(id = R.string.allow),
                            negativeText = stringResource(id = R.string.cancel),
                            positive = {
                                isShowDialog = false
                                launcherPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            },
                            negative = {
                                isShowDialog = false
                                finishAndRemoveTask()
                            },
                        )
                    }
                    if (isShowDialogSetting) {
                        ShowMyDialog(
                            title = stringResource(R.string.permission),
                            msg = stringResource(R.string.permission_msg),
                            positiveText = stringResource(id = R.string.setting),
                            negativeText = stringResource(id = R.string.cancel),
                            positive = {
                                isShowDialogSetting = false
                                requestSettingPermissionLauncher.launch(
                                    Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                                    )
                                )

                            },
                            negative = {
                                isShowDialogSetting = false
                                finishAndRemoveTask()
                            },
                        )
                    }
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                        if (isShowDialogFolder) {
                            ShowMyDialog(
                                title = stringResource(R.string.permission),
                                msg = stringResource(R.string.need_permission_access_folder),
                                positiveText = stringResource(id = R.string.ok),
                                negativeText = stringResource(id = R.string.cancel),
                                positive = {
                                    goToOpenFolder()
                                },
                                negative = { finishAndRemoveTask() },
                            )
                        }
                    }
                    if (!isApplicationInstalled) {
                        ShowMyDialog(
                            title = stringResource(R.string.app_name),
                            msg = stringResource(R.string.install_app),
                            positiveText = stringResource(id = R.string.ok),
                            positive = { finishAndRemoveTask() },
                            isShowDismiss = false
                        )
                    }
                }
            }
        }

    }


    /**
     * Drawer contain
     */

    @Composable
    fun DrawerContain(
        modifier: Modifier, selectedItem: Int
    ) {
        Column(modifier = modifier) {

            val navController = rememberNavController()
            val pagerState = rememberPagerState { listTab.size }

            val scope = rememberCoroutineScope()

            when (selectedItem) {
                0 -> {
                    title = stringResource(R.string.home)
                    LaunchedEffect(scope) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    }
                }

                else -> {
                    title = stringResource(R.string.setting)
                    navController.navigate(Screen.Setting.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            }

            NavHost(navController, startDestination = Screen.Home.route) {

                composable(Screen.Home.route) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TabRow(selectedTabIndex = pagerState.currentPage) {
                            listTab.forEachIndexed { index, title ->
                                Tab(text = {
                                    Text(text = title)
                                }, selected = pagerState.currentPage == index, onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                })
                            }
                        }
                        HorizontalPager(
                            state = pagerState, modifier = Modifier
                                .fillMaxSize()
                                .fillMaxWidth()
                        ) { page ->
                            when (page) {
                                0 -> {
                                    val imageViewModel = hiltViewModel<ImageViewModel>()
                                    Image(this@MainActivity, imageViewModel)
                                }

                                1 -> {
                                    val videoViewModel = hiltViewModel<VideoViewModel>()
                                    Video(this@MainActivity, videoViewModel)
                                }

                                2 -> {
                                    val directory = File(getDownloadPath())
                                    if (directory.exists()) {
                                        val downloadViewModel = hiltViewModel<DownloadViewModel>()
                                        Download(this@MainActivity, downloadViewModel)
                                    } else {
                                        NoDataFound()
                                    }
                                }
                            }
                        }
                    }
                }

                composable(Screen.Setting.route) {

                    Setting(this@MainActivity) {
                        isDark = it
                        isDarkMode = it
                    }
                }

            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun openFolderDialog() {
        CoroutineScope(Dispatchers.IO).launch {
            if (isBothAppInstall) {
                if (myDataStore.getMyDataStoreBoolean(isWhatsAppAllow, defaultValue = false)
                        .first() && myDataStore.getMyDataStoreBoolean(
                        isWhatsAppBusinessAllow, defaultValue = false
                    ).first()
                ) {
                    isShowData = true
                } else {
                    isShowDialogFolder = true
                }
            } else {
                if (myDataStore.getMyDataStoreBoolean(isWhatsAppAllow, defaultValue = false)
                        .first() || myDataStore.getMyDataStoreBoolean(
                        isWhatsAppBusinessAllow, defaultValue = false
                    ).first()
                ) {
                    isShowData = true
                } else {
                    isShowDialogFolder = true
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun goToOpenFolder() {
        CoroutineScope(Dispatchers.IO).launch {
            if (!myDataStore.getMyDataStoreBoolean(
                    isWhatsAppAllow, defaultValue = false
                ).first() && isAppInstalled(whatsAppPackageName)
            ) {
                openFolder(whatsApp11)
            } else if (!myDataStore.getMyDataStoreBoolean(
                    isWhatsAppBusinessAllow, defaultValue = false
                ).first() && isAppInstalled(whatsAppBussinessPackageName)
            ) {
                openFolder(whatsAppBusiness11)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun openFolder(path: String) {

        val storageManager = getSystemService<StorageManager>()!!
        val intent = storageManager.primaryStorageVolume.createOpenDocumentTreeIntent()
        val uri = IntentCompat.getParcelableExtra(
            intent,
            DocumentsContract.EXTRA_INITIAL_URI,
            Uri::class.java
        )
        val encodedPart = path.encodedUrl()
        val scheme = uri.toString().replace("/root/", "/document/") + encodedPart
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, scheme.toUri())
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)

        Log.d("data_information", path)

        resultLauncher.launch(intent)

    }

}
