package com.example.debitter.data

import com.example.debitter.model.Letterhead
import com.example.debitter.model.ShippingAgent

object LetterPresets {
    val letterheads: List<Letterhead> = listOf(
        Letterhead(addressLine = Defaults.company.addressLine, contactLine = Defaults.company.contactLine, name = Defaults.company.name, tagline = ""),
        Letterhead(addressLine = "NO. 101, STEELE ROAD, DANGEDARA, GALLE, SRI LANKA.", contactLine = "", name = "DILEKTA GLASS & MIRROR", tagline = "The Saga of Quality Glass & Mirror"),
        Letterhead(addressLine = "NO. 11, ZAVIA ROAD, HENAMULLA, PANADURA, SRI LANKA.", contactLine = "", name = "GENERAL HARDWARE INDUSTRIES", tagline = "Manufactures of Builder's Hardware"),
        Letterhead(
            addressLine = "NO. 164, U.E. PERERA MAWATHA, OBEYSEKARA TOWN, RAJAGIRIYA, SRI LANKA.",
            contactLine = "TEL: +94 (77) 738 8244, +94 (77) 966 9903 | FAX: 433 4669 | EMAIL: INFO@GRADEWAYS.COM",
            name = "GRADEWAYS",
            tagline = "Customs House & Clearing and Forwarding Agent",
        ),
        Letterhead(addressLine = "NO. 2, HILL STREET, KARAGAMPITIYA, DEHIWALA, SRI LANKA.", contactLine = "TEL: +94 (11) 711 1777 | EMAIL: SALES@HEROLANKA.LK", name = "HERO LANKA (PVT) LTD", tagline = ""),
        Letterhead(addressLine = "NO. 11A, MILE POST AVENUE, COLOMBO - 03, SRI LANKA.", contactLine = "TEL: +94 (11) 488 4457", name = "LANKA COMMODITY HOLDING (PVT) LTD", tagline = ""),
        Letterhead(addressLine = "NO. 819/B, GALLE ROAD, KATUKURUNDA, KALUTARA SOUTH, SRI LANKA.", contactLine = "TEL: +94 (77) 325 3379, +94 (77) 346 7348", name = "NEW KALUTARA GLASS PALACE", tagline = ""),
        Letterhead(addressLine = "NO. 72/A, SAMANALA, NELIGAMA, MIRIGAMA, 11200, SRI LANKA.", contactLine = "TEL: +94 (71) 879 4445 | EMAIL: INFO@NAG.LK", name = "NILDIYA AQUATIC GARDEN", tagline = ""),
        Letterhead(addressLine = "NO. 532/4F, SIRIKOTHA LANE, COLOMBO - 03, SRI LANKA.", contactLine = "TEL: +94 (11) 257 5005 | EMAIL: OCEANPICK@SLTNET.LK", name = "OCEANPICK (PVT) LTD", tagline = ""),
        Letterhead(addressLine = "NO. 22, PEREIRA LANE, WELLAWATTE, COLOMBO - 06, SRI LANKA.", contactLine = "TEL: +94 (11) 236 4445 | EMAIL: MD@OXFORDELEVATORS.LK", name = "OXFORD ELEVATORS COMPANY (PVT) LTD", tagline = ""),
        Letterhead(addressLine = "NO. 101, COTTA ROAD, COLOMBO, SRI LANKA.", contactLine = "TEL: +94 (71) 618 5254 | EMAIL: WORLDOFCARGO@OUTLOOK.COM", name = "SMC ENTERPRISES (PVT) LTD", tagline = ""),
        Letterhead(
            addressLine = "NO. 119, KIRULAPONE AVENUE, COLOMBO - 05, SRI LANKA.",
            contactLine = "TEL: +94 (11) 488 4457 | FAX: +94 (11) 251 2287 | EMAIL: WCHRESOURCES@SLTNET.LK",
            name = "WCH INTERNATIONAL (PVT) LTD",
            tagline = "",
        ),
        Letterhead(
            addressLine = "NO. 6, LAYARD'S ROAD, COLOMBO - 05, SRI LANKA.",
            contactLine = "TEL: +94 (11) 258 5525 | FAX: +94 (11) 258 0941 | EMAIL: WCH2000@SLTNET.LK",
            name = "WHITE CRANE (H2000) AQUA TECH CO.",
            tagline = "Sole Agent for White Crane Aqua Products",
        ),
        Letterhead(addressLine = "NO. 30/32D, DE SILVA CROSS ROAD, KALUBOWILA, DEHIWALA, SRI LANKA.", contactLine = "TEL: +94 (77) 359 7545 | EMAIL: YMI.AUTOHUB@GMAIL.COM", name = "YMI AUTO HUB", tagline = ""),
    )

    val agents: List<ShippingAgent> = listOf(
        ShippingAgent(address = "5th Floor, Pership II\nNo. 35, Edward Lane\nColombo - 03", name = "Asha Shipping Limited"),
        ShippingAgent(address = "5th Floor, HNB Tower\nNo. 479, B Jayah Mawatha\nColombo - 10", name = "CMA-CGM Lanka Private Limited"),
        ShippingAgent(address = "No. 46/12, Nawam Mawatha\nColombo - 02", name = "COSCO Shipping Private Limited"),
        ShippingAgent(address = "No. 101, Vinayakara Mawatha\nColombo - 10", name = "Delshipping & Logistics Private Limited"),
        ShippingAgent(address = "No. 33, Park Street\nColombo - 02", name = "Evergreen Shipping Agency Lanka Private Limited"),
        ShippingAgent(address = "No. 10, Mile Post Avenue\nColombo - 03", name = "Expolanka Freight Private Limited"),
        ShippingAgent(address = "Vauxhall Street\nColombo - 02", name = "Hapag-Lloyd Private Limited"),
        ShippingAgent(address = "No. 40 & 40 1/1, Hudson Road\nColombo - 03", name = "Lanka Shipping & Logistics Private Limited"),
        ShippingAgent(address = "No. 79/1 1/1, 5th Lane\nColombo - 03", name = "Lovikta Logistics Private Limited"),
        ShippingAgent(address = "No. 284, Vauxhall Street\nColombo - 02", name = "McLarens Shipping Limited"),
        ShippingAgent(address = "No. 284, Vauxhall Street\nColombo - 02", name = "McOcean Logistics Private Limited"),
        ShippingAgent(address = "3rd Floor, Lane Building\nNo. 419, Galle Road\nColombo - 02", name = "Mercury Shipping Private Limited"),
        ShippingAgent(address = "No. 121/1, Stace Road\nColombo - 14", name = "MSA Shipping Private Limited"),
        ShippingAgent(address = "Colombo - 03", name = "One Ocean Network Enterprises Private Limited"),
        ShippingAgent(address = "2nd Floor, Robert Senanayeke Building\nNo. 46/5, Nawam Mawatha\nColombo - 02", name = "OOCL Lanka Private Limited"),
        ShippingAgent(address = "No. 99, St. Michael's Road\nColombo - 03", name = "Perma Shipping Lanka Private Limited"),
        ShippingAgent(address = "No. 46/7, Nawam Mawatha\nColombo - 02", name = "Sea Trade Agencies Private Limited"),
        ShippingAgent(address = "1st Floor, Setmil Maritime Centre\nNo. 256, Srimath Ramanathan Mawatha\nColombo - 15", name = "Setmil Logistics Private Limited"),
        ShippingAgent(address = "No. 61/1/1, Balapokuna Road\nColombo - 06", name = "Spedicon Logistics Private Limited"),
        ShippingAgent(address = "No. 45/2, Bray Brooke Street\nColombo - 02", name = "Star Lanka Shipping Private Limited"),
        ShippingAgent(address = "No. 117, Hunupitiya Lake Road\nColombo - 02", name = "Transmarine Private Limited"),
        ShippingAgent(address = "7th Floor\nNo. 193, Danister De Silva Mawatha\nColombo - 08", name = "Unifeeder Lanka Private Limited"),
    )
}
