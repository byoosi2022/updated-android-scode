package com.byoosi.pos.utils

import android.content.Context
import android.os.Build
import java.util.*

/**
 * Created by DK on 05-10-2019.
 */
object CountryUtil {

    private val map = HashMap<String, Any>()

    init {
        map["AD"] = "376"
        map["AE"] = "971"
        map["AF"] = "93"
        map["AG"] = "1"
        map["AI"] = "1"
        map["AL"] = "355"
        map["AM"] = "374"
        map["AN"] = "599"
        map["AO"] = "244"
        map["AR"] = "54"
        map["AS"] = "1"
        map["AT"] = "43"
        map["AU"] = "61"
        map["AW"] = "297"
        map["AZ"] = "994"
        map["BA"] = "387"
        map["BB"] = "1"
        map["BD"] = "880"
        map["BE"] = "32"
        map["BF"] = "226"
        map["BG"] = "359"
        map["BH"] = "973"
        map["BI"] = "257"
        map["BJ"] = "229"
        map["BL"] = "590"
        map["BM"] = "1"
        map["BN"] = "673"
        map["BO"] = "591"
        map["BR"] = "55"
        map["BS"] = "1"
        map["BT"] = "975"
        map["BW"] = "267"
        map["BY"] = "375"
        map["BZ"] = "501"
        map["CA"] = "1"
        map["CC"] = "61"
        map["CD"] = "243"
        map["CF"] = "236"
        map["CG"] = "242"
        map["CH"] = "41"
        map["CI"] = "225"
        map["CK"] = "682"
        map["CL"] = "56"
        map["CM"] = "237"
        map["CN"] = "86"
        map["CO"] = "57"
        map["CR"] = "506"
        map["CU"] = "53"
        map["CV"] = "238"
        map["CX"] = "61"
        map["CY"] = "537"
        map["CZ"] = "420"
        map["DE"] = "49"
        map["DJ"] = "253"
        map["DK"] = "45"
        map["DM"] = "1"
        map["DO"] = "1"
        map["DZ"] = "213"
        map["EC"] = "593"
        map["EE"] = "372"
        map["EG"] = "20"
        map["ER"] = "291"
        map["ES"] = "34"
        map["ET"] = "251"
        map["FI"] = "358"
        map["FJ"] = "679"
        map["FK"] = "500"
        map["FM"] = "691"
        map["FO"] = "298"
        map["FR"] = "33"
        map["GA"] = "241"
        map["GB"] = "44"
        map["GD"] = "1"
        map["GE"] = "995"
        map["GF"] = "594"
        map["GG"] = "44"
        map["GH"] = "233"
        map["GI"] = "350"
        map["GL"] = "299"
        map["GM"] = "220"
        map["GN"] = "224"
        map["GP"] = "590"
        map["GQ"] = "240"
        map["GR"] = "30"
        map["GS"] = "500"
        map["GT"] = "502"
        map["GU"] = "1"
        map["GW"] = "245"
        map["GY"] = "595"
        map["HK"] = "852"
        map["HN"] = "504"
        map["HR"] = "385"
        map["HT"] = "509"
        map["HU"] = "36"
        map["ID"] = "62"
        map["IE"] = "353"
        map["IL"] = "972"
        map["IM"] = "44"
        map["IN"] = "91"
        map["IO"] = "246"
        map["IQ"] = "964"
        map["IR"] = "98"
        map["IS"] = "354"
        map["IT"] = "39"
        map["JE"] = "44"
        map["JM"] = "1"
        map["JO"] = "962"
        map["JP"] = "81"
        map["KE"] = "254"
        map["KG"] = "996"
        map["KH"] = "855"
        map["KI"] = "686"
        map["KM"] = "269"
        map["KN"] = "1"
        map["KP"] = "850"
        map["KR"] = "82"
        map["KW"] = "965"
        map["KY"] = "345"
        map["KZ"] = "77"
        map["LA"] = "856"
        map["LB"] = "961"
        map["LC"] = "1"
        map["LI"] = "423"
        map["LK"] = "94"
        map["LR"] = "231"
        map["LS"] = "266"
        map["LT"] = "370"
        map["LU"] = "352"
        map["LV"] = "371"
        map["LY"] = "218"
        map["MA"] = "212"
        map["MC"] = "377"
        map["MD"] = "373"
        map["ME"] = "382"
        map["MF"] = "590"
        map["MG"] = "261"
        map["MH"] = "692"
        map["MK"] = "389"
        map["ML"] = "223"
        map["MM"] = "95"
        map["MN"] = "976"
        map["MO"] = "853"
        map["MP"] = "1"
        map["MQ"] = "596"
        map["MR"] = "222"
        map["MS"] = "1"
        map["MT"] = "356"
        map["MU"] = "230"
        map["MV"] = "960"
        map["MW"] = "265"
        map["MX"] = "52"
        map["MY"] = "60"
        map["MZ"] = "258"
        map["NA"] = "264"
        map["NC"] = "687"
        map["NE"] = "227"
        map["NF"] = "672"
        map["NG"] = "234"
        map["NI"] = "505"
        map["NL"] = "31"
        map["NO"] = "47"
        map["NP"] = "977"
        map["NR"] = "674"
        map["NU"] = "683"
        map["NZ"] = "64"
        map["OM"] = "968"
        map["PA"] = "507"
        map["PE"] = "51"
        map["PF"] = "689"
        map["PG"] = "675"
        map["PH"] = "63"
        map["PK"] = "92"
        map["PL"] = "48"
        map["PM"] = "508"
        map["PN"] = "872"
        map["PR"] = "1"
        map["PS"] = "970"
        map["PT"] = "351"
        map["PW"] = "680"
        map["PY"] = "595"
        map["QA"] = "974"
        map["RE"] = "262"
        map["RO"] = "40"
        map["RS"] = "381"
        map["RU"] = "7"
        map["RW"] = "250"
        map["SA"] = "966"
        map["SB"] = "677"
        map["SC"] = "248"
        map["SD"] = "249"
        map["SE"] = "46"
        map["SG"] = "65"
        map["SH"] = "290"
        map["SI"] = "386"
        map["SJ"] = "47"
        map["SK"] = "421"
        map["SL"] = "232"
        map["SM"] = "378"
        map["SN"] = "221"
        map["SO"] = "252"
        map["SR"] = "597"
        map["ST"] = "239"
        map["SV"] = "503"
        map["SY"] = "963"
        map["SZ"] = "268"
        map["TC"] = "1"
        map["TD"] = "235"
        map["TG"] = "228"
        map["TH"] = "66"
        map["TJ"] = "992"
        map["TK"] = "690"
        map["TL"] = "670"
        map["TM"] = "993"
        map["TN"] = "216"
        map["TO"] = "676"
        map["TR"] = "90"
        map["TT"] = "1"
        map["TV"] = "688"
        map["TW"] = "886"
        map["TZ"] = "255"
        map["UA"] = "380"
        map["UG"] = "256"
        map["US"] = "1"
        map["UY"] = "598"
        map["UZ"] = "998"
        map["VA"] = "379"
        map["VC"] = "1"
        map["VE"] = "58"
        map["VG"] = "284"
        map["VI"] = "340"
        map["VN"] = "84"
        map["VU"] = "678"
        map["WF"] = "681"
        map["WS"] = "685"
        map["YE"] = "967"
        map["YT"] = "262"
        map["ZA"] = "27"
        map["ZM"] = "260"
        map["ZW"] = "263"
    }

    fun getCountryCode(context: Context): String {

        val countryNameCode: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0).country
        } else {
            context.resources.configuration.locale.country
        }

        val code = map[countryNameCode]

        return if (code != null) {
            "+$code"
        } else ""
    }
}