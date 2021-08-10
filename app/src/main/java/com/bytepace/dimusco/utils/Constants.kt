package com.bytepace.dimusco.utils

const val REST_API_ENDPOINT = "https://d-w-ul.dimusco.com/"
const val SOCKET_ENDPOINT = "wss://d-w-sy.dimusco.com:8000/socket/"

const val EDITOR_TEMP_FOLDER = "Editor"

const val NAV_ARG_SCORE_ID = "scoreId"
const val NAV_ARG_SCORE_AID = "scoreAid"
const val NAV_ARG_PAGE = "page"
const val NAV_ARG_NEED_GENERAL = "needGeneral"
const val NAV_ARG_SCORE = "scoreInstance"
const val NAV_ARG_MARKER = "markerInstance"
const val NAV_ARG_LAYER = "layerInstance"
const val NAV_ARG_MESSAGE = "message"
const val NAV_ARG_YES_WORD = "yesWord"
const val NAV_ARG_NO_WORD = "noWord"

const val NAV_ARG_NEED_SEEK_BAR = "needSeekBar"

const val THUMBNAILS_DIR = "thumbnails/"

const val BASE_BRUSH_SIZE = 30f
// TODO change type to float
const val IMAGE_SIZE_MAX = 1920
const val LAYER_SIZE_MAX = 1024f
const val LINE_THICKNESS_MIN = 0.02f
const val LINE_THICKNESS_MAX = 1.2f
const val ERASER_THICKNESS_MIN = 0.1f
const val ERASER_THICKNESS_MAX = 1.2f

const val LANG_EN = "en"
const val LAYER_MIN_POS = 10

const val LAYER_TYPE_READ_WRITE = 2
const val LAYER_TYPE_READ_ONLY = 1

const val MESSAGE_TYPE_DOWNLOAD = "download"
const val MESSAGE_TYPE_LAYERS = "layerList"
const val MESSAGE_TYPE_MARKER = "MarkerSync"
const val MESSAGE_TYPE_SETTINGS = "syncSettings"
const val MESSAGE_TYPE_SCORE = "bookLib"
const val MESSAGE_SYNC_TABS = "syncTabs"
const val MESSAGE_TYPE_LAYER_CHANGE = "layerChange"
const val MESSAGE_TYPE_LAYER_CREATE = "layerCreate"
const val MESSAGE_TYPE_LAYER_DELETE = "layerDelete"
const val MESSAGE_TYPE_LAYER_PAGE_SYNC = "layerPageSync"
const val MESSAGE_TYPE_DOWNLOAD_PAGE = "downloadPages"
const val MESSAGE_TYPE_WINDOW_SYNC = "pageWindowsSync"

const val FLOAT_FORMAT = "#.##"

const val PICTURE_TYPE_IMAGE = "DrawingImage"
const val PICTURE_TYPE_BRUSH = "Brush"
const val PICTURE_TYPE_SYMBOL = "Symbol"
const val PICTURE_TYPE_TEXT = "Text"

const val STR_COLORS_SET = "colors_setColor"
const val STR_EDIT_TIMEOUT = "edit_link_timeout"
const val STR_ERR_LOGIN_EMAIL = "login_pleaseEnterEmail"
const val STR_ERR_LOGIN_OFFLINE = "alert_offlineWrongPassword"
const val STR_ERR_LOGIN_PASSWORD = "login_pleaseEnterPassword"
const val STR_GLOBAL_CANCEL = "global_cancel"
const val STR_GLOBAL_COLORS = "global_colors"
const val STR_GLOBAL_COMPOSER_ASC = "global_composerAscending"
const val STR_GLOBAL_COMPOSER_DESC = "global_composerDescending"
const val STR_GLOBAL_DEFAULTS = "global_defaults"
const val STR_GLOBAL_DIMUSCO = "global_dimusco"
const val STR_GLOBAL_EDITORS = "global_editors"
const val STR_GLOBAL_EMAIL = "global_email"
const val STR_GLOBAL_ERASER_THICKNESS = "global_eraserThickness"
const val STR_GLOBAL_ERROR = "global_error"
const val STR_GLOBAL_GENERAL = "global_general"
const val STR_GLOBAL_GLOBAL_SELECTION = "global_globalSelection"
const val STR_GLOBAL_LANGUAGE = "global_language"
const val STR_GLOBAL_LAYER = "global_layer"
const val STR_GLOBAL_LAYERS = "global_layers"
const val STR_GLOBAL_LINE_THICKNESS = "global_lineThickness"
const val STR_GLOBAL_LOGIN = "global_login"
const val STR_GLOBAL_NAME_ASC = "global_nameAscending"
const val STR_GLOBAL_NAME_DESC = "global_nameDescending"
const val STR_GLOBAL_NO = "global_no"
const val STR_GLOBAL_OK = "global_ok"
const val STR_GLOBAL_PASSWORD = "global_password"
const val STR_GLOBAL_PERSONAL_SELECTION = "global_personalSelection"
const val STR_GLOBAL_RESET = "global_reset"
const val STR_GLOBAL_SEQUENCE = "global_sequence"
const val STR_GLOBAL_DEACTIVATE = "global_deactivate"
const val STR_GLOBAL_ACTIVATE = "global_activate"
const val STR_GLOBAL_SETTINGS = "global_settings"
const val STR_GLOBAL_STORE = "global_store"
const val STR_GLOBAL_SYMBOLS = "global_symbols"
const val STR_GLOBAL_SYMBOL_SIZE = "global_symbolSize"
const val STR_GLOBAL_TEXT_SIZE = "global_textSize"
const val STR_GLOBAL_TRANSPARENCY = "global_transparency"
const val STR_GLOBAL_WAIT = "global_wait"
const val STR_GLOBAL_YES = "global_yes"
const val STR_LAYER_ADD = "layersSettings_add"
const val STR_LAYER_ADD_PROMPT = "layersSettings_pleaseAddLayer"
const val STR_LAYER_DELETE = "layersSettings_doYouWantToDelete"
const val STR_LAYER_DELETE_SUCCESS = "layersSettings_deleteSuccess"
const val STR_LAYER_DRAWINGS = "layer_drawings_image"
const val STR_LAYER_EDIT_NAME = "layersSettings_editLayerName"
const val STR_LAYER_IMPORT = "layersSettings_import"
const val STR_LAYER_MODES = "layer_modes"
const val STR_LAYER_NAME = "layersSettings_layerName"
const val STR_LAYER_NAME_PROMPT = "layersSettings_pleaseAddName"
const val STR_LAYER_SYMBOLS = "layer_symbols_image"
const val STR_LAYER_TEXT = "layer_text_image"
const val STR_LIBRARY_EMPTY = "empty_library"
const val STR_LOGIN_CREATE_ACCOUNT = "login_createAccount" //why is it giving something different , i guess because constant connected to socket
const val STR_LOGIN_RESET_PASSWORD = "login_forgotPassword"
const val STR_LOGIN_RESTORE = "login_restore"
const val STR_LOGIN_REMEMBER_PASSWORD = "login_rememberPassword"
const val STR_LOGIN_SIGN_IN = "login_signInToContinue"
const val STR_LOGIN_WELCOME = "login_welcomeBack"
const val STR_LOGOUT = "logout_areYouSure"
const val STR_LOGOUT_YES = "logout_yes"
const val STR_RESTORE_APP = "restore_doYouWant"
const val STR_SCORE_REMOVE = "remove_score"
const val STR_EDITOR_SAVE = "editor_doYouWantToSave"
const val STR_SELECT_LANGUAGE = "languageSettings_selectLanguage"
const val STR_SETTINGS_CENTRAL_ACCEPT = "generalSettings_receiveSettings"
const val STR_SETTINGS_CENTRAL_UPDATE = "generalSettings_sendSettings"
const val STR_SETTINGS_CONFIRM = "generalSettings_confirmEdit"
const val STR_SETTINGS_DIRECTION = "generalSettings_pageChangeDirection"
const val STR_SETTINGS_HORIZONTAL = "generalSettings_horizontal"
const val STR_SETTINGS_SEQUENCE = "generalSettings_enterSequence"
const val STR_SETTINGS_VERTICAL = "generalSettings_vertical"
const val STR_MARKER_SETTINGS_ADD = "markerSettings_add"
const val STR_MARKER_SETTINGS_EDIT = "markerSettings_edit"
const val STR_MARKER_SETTINGS_REMOVE = "markerSettings_remove"

const val STR_DOWNLOADING = "Downloading"

val GLOBAL_COLORS = listOf(
    0x000000, 0xff0000, 0x00ff00, 0x0000ff, 0xffff00, 0x00ffff, 0xff00ff, 0xffffff,
    0xc0c0c0, 0x808080, 0x800000, 0x808000, 0x008000, 0x800080, 0x008080, 0x000080
)