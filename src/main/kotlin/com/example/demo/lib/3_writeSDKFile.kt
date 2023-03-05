package com.example.demo.lib

import com.example.demo.lib.types.HandlerMetadata
import com.example.demo.lib.types.TypeName
import com.example.demo.lib.utils.BuildTSFunctionHandlerMD
import com.example.demo.lib.utils.ExtendedTypeInformation
import com.example.demo.lib.utils.buildTSFunction
import java.io.File

fun writeSDKFile(
    handlersMD: List<HandlerMetadata>,
    userTypes: Set<TypeName>,
    outFilePath: String
) {
    val transformedHandlersMD: Map<String, List<BuildTSFunctionHandlerMD>> =
        handlersMD
            .groupBy { it.controllerName }
            .mapValues { (_, handlers) ->
                handlers.map { handler ->
                    BuildTSFunctionHandlerMD(
                        path = handler.path,
                        functionName = handler.functionName,
                        method = handler.method,
                        paramsType = handler.paramsType.ifEmpty { null },
                        queryStringType = handler.queryStringType.ifEmpty { null },
                        requestBodyType = if (handler.requestBodyType == null) {
                            null
                        } else {
                            ExtendedTypeInformation(
                                type = handler.requestBodyType,
                                isUserType = userTypes.contains(handler.requestBodyType.simpleName)
                            )
                        },
                        successResponseType = ExtendedTypeInformation(
                            type = handler.successResponseType,
                            isUserType = userTypes.contains(handler.successResponseType.simpleName)
                        ),
                        errorResponseType = ExtendedTypeInformation(
                            type = handler.errorResponseType,
                            isUserType = userTypes.contains(handler.errorResponseType.simpleName)
                        )
                    )
                }
            }

    // Write to SDK file
    val sdkFileContents = StringBuilder()

    // Append imports
    sdkFileContents.appendLine("import UserTypes from \"./UserTypes\";")
    sdkFileContents.appendLine("import { ServerResponse } from \"./ServerResponse\";")
    sdkFileContents.appendLine("import { replacePathAndQueryVariables } from \"./Utils\";")
    sdkFileContents.appendLine()

    // Append SDK factory
    sdkFileContents.appendLine("export default function BuildSDK(apiBaseUrl: string) { return {")

    for ((controllerName, controllerHandlers) in transformedHandlersMD) {
        sdkFileContents.appendLine("\t${controllerName.lowercase()}: {")

        for (handler in controllerHandlers)
            sdkFileContents.appendLine("\t\t${handler.functionName}: ${buildTSFunction(handler)},")

        sdkFileContents.appendLine("\t},")
    }
    sdkFileContents.appendLine("}}")

    File(outFilePath)
        .writeText(sdkFileContents.toString())
}
