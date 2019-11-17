package com.invictus.unichat.about

import android.content.Context
import android.graphics.Color
import android.net.Uri

import com.danielstone.materialaboutlibrary.ConvenienceBuilder
import com.danielstone.materialaboutlibrary.MaterialAboutActivity
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard
import com.danielstone.materialaboutlibrary.model.MaterialAboutList

import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial
import com.mikepenz.iconics.utils.toIconicsColor
import com.mikepenz.iconics.sizeDp

import com.invictus.unichat.R


class AboutActivity : MaterialAboutActivity() {

    override fun getMaterialAboutList(context: Context): MaterialAboutList {

        // the builder for the first card
        val appCardBuilder = MaterialAboutCard.Builder()
        // the builder for the author list
        val authorCardBuilder = MaterialAboutCard.Builder()
        authorCardBuilder.title(R.string.card_title_authors)

        // the app title and icon
        appCardBuilder.addItem(
            MaterialAboutTitleItem.Builder()
                .text(R.string.card_app_title)
                .desc(R.string.card_app_desc)
                .icon(R.mipmap.ic_bare)
                .build()
        )

        // the app version and version-code
        appCardBuilder.addItem(
            ConvenienceBuilder.createVersionActionItem(context,
            IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_info_outline)
                .color(Color.WHITE.toIconicsColor())
                .sizeDp(18),
            "Version",
            true))

        // the app changelog
        appCardBuilder.addItem(
            MaterialAboutActionItem.Builder()
                .text(R.string.card_changelog_string)
                .icon(
                    IconicsDrawable(context)
                        .icon(GoogleMaterial.Icon.gmd_history)
                        .color(Color.WHITE.toIconicsColor())
                        .sizeDp(18)
                )
                .setOnClickAction(
                    ConvenienceBuilder.createWebViewDialogOnClickAction(
                        context,
                        getString(R.string.card_changelog_string),
                        getString(R.string.card_changelog_link),
                        true,
                        false
                    )
                )
                .build()
        )

        // the app license
        appCardBuilder.addItem(
            MaterialAboutActionItem.Builder()
                .text(R.string.card_license_string)
                .icon(
                    IconicsDrawable(context)
                        .icon(GoogleMaterial.Icon.gmd_book)
                        .color(Color.WHITE.toIconicsColor())
                        .sizeDp(18)
                )
                .setOnClickAction(
                    ConvenienceBuilder.createWebViewDialogOnClickAction(
                        context,
                        getString(R.string.card_license_webview_header),
                        getString(R.string.card_license_link),
                        true,
                        false
                    )
                )
                .build()
        )

        // the first author
        authorCardBuilder.addItem(
            MaterialAboutActionItem.Builder()
                .text(R.string.author_one_real)
                .subText(R.string.author_one_nick)
                .icon(
                    IconicsDrawable(context)
                        .icon(GoogleMaterial.Icon.gmd_account_circle)
                        .color(Color.WHITE.toIconicsColor())
                        .sizeDp(18)
                )
                .build()
        )

        // the second author
        authorCardBuilder.addItem(
            MaterialAboutActionItem.Builder()
                .text(R.string.author_two_real)
                .subText(R.string.author_two_nick)
                .icon(
                    IconicsDrawable(context)
                        .icon(GoogleMaterial.Icon.gmd_account_circle)
                        .color(Color.WHITE.toIconicsColor())
                        .sizeDp(18)
                )
                .build()
        )

        // the third author
        authorCardBuilder.addItem(
            MaterialAboutActionItem.Builder()
                .text(R.string.author_three_real)
                .subText(R.string.author_three_nick)
                .icon(
                    IconicsDrawable(context)
                        .icon(GoogleMaterial.Icon.gmd_account_circle)
                        .color(Color.WHITE.toIconicsColor())
                        .sizeDp(18)
                )
                .build()
        )

        // github link
        authorCardBuilder.addItem(
            MaterialAboutActionItem.Builder()
                .text(R.string.card_repo_string)
                .icon(
                    IconicsDrawable(context)
                        .icon(GoogleMaterial.Icon.gmd_code)
                        .color(Color.WHITE.toIconicsColor())
                        .sizeDp(18)
                )
                .setOnClickAction(
                    ConvenienceBuilder.createWebsiteOnClickAction(
                        context,
                        Uri.parse(getString(R.string.card_repo_link))
                    )
                )
                .build()
        )

        // putting it all together
        return MaterialAboutList(
            appCardBuilder.build(),
            authorCardBuilder.build()
        )

    }

    // set a title
    override fun getActivityTitle(): CharSequence? {
        return getString(R.string.title_about_activity)
    }

}