package com.semgrep.idea.settings

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*

class AppSettingsComponent(settings: SemgrepLspSettings) {
    private var panel: DialogPanel? = null

    // Really we should use reflection to generate this, but I'm lazy
    private val traceChooser = ComboBox(arrayOf("off", "messages", "verbose"))
    private val pathTextField = JBTextField()
    private val ignoreCliVersion = JBCheckBox()
    private val doHover = JBCheckBox()

    // Scan Settings
    private val configuration = JBTextField()
    private val exclude = JBTextField()
    private val include = JBTextField()
    private val jobs = JBTextField("1")
    private val maxMemory = JBTextField("0")
    private val maxTargetBytes = JBTextField("1000000")
    private val timeout = JBTextField("30")
    private val timeoutThreshold = JBTextField("3")
    private val onlyGitDirty = JBCheckBox()

    // Metric Settings
    private val metricsEnabled = JBCheckBox()
    private var lspSettings: SemgrepLspSettings = AppState.getInstance().lspSettings

    init {
        panel = panel {
            group("General") {
                row("Trace Level") {
                    comboBox(TraceLevel.values().toList())
                        .bindItem(lspSettings.trace::server.toNullableProperty())
                        .comment("Traces the communication between IntelliJ and the language server.")
                }
                row("Semgrep Path") {
                    textField()
                        .bindText(lspSettings::path)
                        .comment("Path to the semgrep executable.")
                }
                row {
                    checkBox("Ignore Cli Version")
                        .bindSelected(lspSettings::ignoreCliVersion)
                        .comment("Ignore CLI Version, and enable all extension features (Warning: this is mainly for extension development, and can break things if enabled!)")
                }
                row {
                    checkBox("Do Hover")
                        .bindSelected(lspSettings::doHover)
                        .comment("Enable hovering for AST node viewing (requires restart)")
                }
            }
            group("Scan") {
                row("Configuration") {
                    textField()
                        .bindText({ lspSettings.scan.configuration.joinToString(",") }, { s ->
                            lspSettings.scan.configuration =
                                s.split(",").map { it.trim() }.toTypedArray()
                        })
                        .comment("Each item can be a YAML configuration file, directory of YAML files ending in .yml | .yaml, URL of a configuration file, or Semgrep registry entry name. Use \"auto\" to automatically obtain rules tailored to this project; your project URL will be used to log in to the Semgrep registry. Must be a comma-separated list.")

                }
                row("Exclude") {
                    textField()
                        .bindText({ lspSettings.scan.exclude.joinToString(",") }, { s ->
                            lspSettings.scan.exclude =
                                s.split(",").map { it.trim() }.toTypedArray()
                        })
                        .comment("List of files or directories to exclude. Must be a comma-separated list.")
                }
                row("Include") {
                    textField()
                        .bindText({ lspSettings.scan.include.joinToString(",") }, { s ->
                            lspSettings.scan.include =
                                s.split(",").map { it.trim() }.toTypedArray()
                        })
                        .comment("List of files or directories to include. Must be a comma-separated list.")
                }
                row("Jobs") {
                    spinner(0..100)
                        .bindIntValue(lspSettings.scan::jobs)
                        .comment("Number of parallel jobs to run.")
                }
                row("Max Memory") {
                    spinner(0..1000)
                        .bindIntValue(lspSettings.scan::maxMemory)
                        .comment("Maximum memory to use in megabytes.")
                }
                row("Max Target Bytes") {
                    spinner(0..(Math.pow(10.0, 10.0).toInt()))
                        .bindIntValue(lspSettings.scan::maxTargetBytes)
                        .comment("Maximum size of target in bytes to scan.")
                }
                row("Timeout") {
                    spinner(0..1000)
                        .bindIntValue(lspSettings.scan::timeout)
                        .comment("Maximum time to scan in seconds.")
                }
                row("Timeout Threshold") {
                    spinner(0..1000)
                        .bindIntValue(lspSettings.scan::timeoutThreshold)
                        .comment("Maximum number of rules that can timeout on a file before the file is skipped. If set to 0 will not have limit. Defaults to 3.")
                }
                row {
                    checkBox("Only Git Dirty")
                        .bindSelected(lspSettings.scan::onlyGitDirty)
                        .comment("Only scan lines changed since the last commit")
                }
            }
            group("Metrics") {
                row {
                    checkBox("Enabled")
                        .bindSelected(lspSettings.metrics::enabled)
                        .comment("Enable metrics reporting")
                }
            }
        }

    }

    fun getPanel(): DialogPanel? {
        return panel
    }

}