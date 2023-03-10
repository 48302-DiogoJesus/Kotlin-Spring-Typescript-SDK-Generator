import UserTypes from "./UserTypes";
import { HandlerResponse } from "./HandlerResponse";
import { replacePathAndQueryVariables } from "./Utils";

export default function BuildSDK(apiBaseUrl: string) { return {
	users: {
		get: (args: { 
	
	id: string,
	a: string,
	b: string
,
}): Promise<HandlerResponse<UserTypes.User, UserTypes.ErrorFormat>> => 
	fetch(
		replacePathAndQueryVariables(`${apiBaseUrl}/api/users/{id}/{a}/{b}`, args, undefined),
		{
			method: "GET",
		}
	)
		.then(res => res.json())
,
		delete: (args: { 
	
	id: string
,
}): Promise<HandlerResponse<void, UserTypes.ErrorFormat>> => 
	fetch(
		replacePathAndQueryVariables(`${apiBaseUrl}/api/users/{id}`, args, undefined),
		{
			method: "DELETE",
		}
	)
		.then(res => res.json())
,
		create: (args: { 
	} & UserTypes.CreateUserModel
): Promise<HandlerResponse<UserTypes.User, UserTypes.ErrorFormat>> => 
	fetch(
		replacePathAndQueryVariables(`${apiBaseUrl}/api/users/`, undefined, undefined),
		{
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(args)
		}
	)
		.then(res => res.json())
,
	},
	posts: {
		get: (args: { 
	
	id: string
,
}): Promise<UserTypes.Post> => 
	fetch(
		replacePathAndQueryVariables(`${apiBaseUrl}/api/posts/{id}`, args, undefined),
		{
			method: "GET",
		}
	)
		.then(res => res.json())
,
		create: (args: { 
	} & UserTypes.CreatePostModel
): Promise<HandlerResponse<UserTypes.Post, UserTypes.ErrorFormat>> => 
	fetch(
		replacePathAndQueryVariables(`${apiBaseUrl}/api/posts/`, undefined, undefined),
		{
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(args)
		}
	)
		.then(res => res.json())
,
	},
}}
