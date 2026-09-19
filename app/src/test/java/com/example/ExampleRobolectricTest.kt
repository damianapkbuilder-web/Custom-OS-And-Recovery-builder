package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.DeviceRepository
import com.example.data.model.OsRepository
import com.example.data.model.RecoveryRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Recovery & ROM Studio", appName)
  }

  @Test
  fun `verify all 10 recoveries are selectable`() {
    val recoveries = RecoveryRepository.recoveries
    assertEquals(10, recoveries.size)
    assertNotNull(recoveries.find { it.id == "amon_ra" })
    assertNotNull(recoveries.find { it.id == "cwm" })
    assertNotNull(recoveries.find { it.id == "twrp" })
    assertNotNull(recoveries.find { it.id == "orangefox" })
    assertNotNull(recoveries.find { it.id == "pbrp" })
    assertNotNull(recoveries.find { it.id == "redwolf" })
    assertNotNull(recoveries.find { it.id == "shrp" })
    assertNotNull(recoveries.find { it.id == "lineage_rec" })
    assertNotNull(recoveries.find { it.id == "safestrap" })
    assertNotNull(recoveries.find { it.id == "pterodon" })
  }

  @Test
  fun `verify custom operating systems and ubuntu touch`() {
    val osList = OsRepository.operatingSystems
    assertTrue(osList.size >= 20)
    assertNotNull(osList.find { it.id == "cyanogenmod" })
    assertNotNull(osList.find { it.id == "lineageos" })
    assertNotNull(osList.find { it.id == "ubuntu_touch" })
    assertNotNull(osList.find { it.id == "grapheneos" })
    assertNotNull(osList.find { it.id == "calyxos" })
    assertNotNull(osList.find { it.id == "pixel_experience" })
    assertNotNull(osList.find { it.id == "resurrection_remix" })
  }

  @Test
  fun `verify cupcake device selectable`() {
    val devices = DeviceRepository.devices
    val cupcakeDevice = devices.find { it.id == "htc_dream" }
    assertNotNull(cupcakeDevice)
    assertTrue(cupcakeDevice!!.androidVersionBadge.contains("Cupcake"))
    assertEquals("armeabi", cupcakeDevice.nativeArch)
  }

  @Test
  fun `verify all Galaxy S3 variants are present with model numbers and wifi details`() {
    val devices = DeviceRepository.devices
    val s3Variants = devices.filter { it.id.startsWith("galaxy_s3") }
    // We should have at least 12 distinct Galaxy S3 variants
    assertTrue(s3Variants.size >= 12)

    val intl3g = devices.find { it.id == "galaxy_s3_i9300" }
    assertNotNull(intl3g)
    assertEquals("GT-I9300", intl3g!!.modelNumber)
    assertTrue(intl3g.codename.contains("i9300"))

    val intlLte = devices.find { it.id == "galaxy_s3_i9305" }
    assertNotNull(intlLte)
    assertEquals("GT-I9305", intlLte!!.modelNumber)

    val att = devices.find { it.id == "galaxy_s3_att" }
    assertNotNull(att)
    assertEquals("SGH-I747", att!!.modelNumber)

    val tmo = devices.find { it.id == "galaxy_s3_tmobile" }
    assertNotNull(tmo)
    assertEquals("SGH-T999 / T999L", tmo!!.modelNumber)

    val vzw = devices.find { it.id == "galaxy_s3_verizon" }
    assertNotNull(vzw)
    assertEquals("SCH-I535", vzw!!.modelNumber)

    val spr = devices.find { it.id == "galaxy_s3_sprint" }
    assertNotNull(spr)
    assertEquals("SPH-L710 / L710T", spr!!.modelNumber)

    val usc = devices.find { it.id == "galaxy_s3_uscellular" }
    assertNotNull(usc)
    assertEquals("SCH-R530", usc!!.modelNumber)

    val can = devices.find { it.id == "galaxy_s3_canada" }
    assertNotNull(can)
    assertEquals("SGH-I747M", can!!.modelNumber)

    val kor = devices.find { it.id == "galaxy_s3_korea_lte" }
    assertNotNull(kor)
    assertTrue(kor!!.modelNumber.contains("SHV-E210"))

    val dcm = devices.find { it.id == "galaxy_s3_japan" }
    assertNotNull(dcm)
    assertEquals("SC-06D", dcm!!.modelNumber)

    val alpha = devices.find { it.id == "galaxy_s3_japan_alpha" }
    assertNotNull(alpha)
    assertEquals("SC-03E", alpha!!.modelNumber)

    val neo = devices.find { it.id == "galaxy_s3_neo" }
    assertNotNull(neo)
    assertTrue(neo!!.modelNumber.contains("GT-I9301I"))

    val mini = devices.find { it.id == "galaxy_s3_mini" }
    assertNotNull(mini)
    assertTrue(mini!!.modelNumber.contains("GT-I8190"))

    // Ensure all Galaxy S3 variants provide available target Wi-Fi networks
    s3Variants.forEach { s3 ->
      assertTrue(s3.availableWifiNetworks.isNotEmpty())
      assertTrue(s3.defaultTargetWifiSsid.isNotBlank())
    }
  }

  @Test
  fun `verify LineageOS 22 23 and 24 support`() {
    val lineage = OsRepository.operatingSystems.find { it.id == "lineageos" }
    assertNotNull(lineage)
    val versions = lineage!!.defaultVersions
    assertTrue(versions.any { it.contains("22") })
    assertTrue(versions.any { it.contains("23") })
    assertTrue(versions.any { it.contains("24") })

    val lineageMicroG = OsRepository.operatingSystems.find { it.id == "lineage_microg" }
    assertNotNull(lineageMicroG)
    assertTrue(lineageMicroG!!.defaultVersions.any { it.contains("22") })
    assertTrue(lineageMicroG.defaultVersions.any { it.contains("23") })
    assertTrue(lineageMicroG.defaultVersions.any { it.contains("24") })
  }

  @Test
  fun `verify all root suites and ALL_ROOTS bundle are available`() {
    val options = com.example.data.model.MagiskOption.entries
    assertNotNull(options.find { it == com.example.data.model.MagiskOption.ALL_ROOTS })
    assertNotNull(options.find { it == com.example.data.model.MagiskOption.MAGISK_DELTA })
    assertNotNull(options.find { it == com.example.data.model.MagiskOption.MAGISK_ALPHA })
    assertNotNull(options.find { it == com.example.data.model.MagiskOption.KERNEL_SU })
    assertNotNull(options.find { it == com.example.data.model.MagiskOption.APATCH })
    assertNotNull(options.find { it == com.example.data.model.MagiskOption.SUPERSU })
  }

  @Test
  fun `verify web search discovery engine queries for steps 2 3 4 and 5`() = kotlinx.coroutines.runBlocking {
    val recoveries = com.example.data.repository.WebSearchDiscoveryEngine.searchRecoveryOnline("")
    assertTrue(recoveries.isNotEmpty())

    val osResults = com.example.data.repository.WebSearchDiscoveryEngine.searchOsOnline("LineageOS")
    assertTrue(osResults.any { it.title.contains("LineageOS 24") || it.title.contains("LineageOS 23") || it.title.contains("LineageOS 22") })

    val versionResults = com.example.data.repository.WebSearchDiscoveryEngine.searchOsVersionsOnline("LineageOS", "24")
    assertTrue(versionResults.any { it.versionTag.contains("24") })

    val modResults = com.example.data.repository.WebSearchDiscoveryEngine.searchModsAndRootsOnline("")
    assertTrue(modResults.any { it.versionTag == "all_roots" })
    assertTrue(modResults.any { it.versionTag == "kitsune" })
    assertTrue(modResults.any { it.versionTag == "kernelsu" })
  }
}
