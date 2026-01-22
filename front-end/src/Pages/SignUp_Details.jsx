import { useState } from "react"
export default function SignUp_Details(){
    const [phoneNumber, setPhoneNumber] = useState("");
    const [address, setAddress] = useState("");
    const [companyName, setCompanyName] = useState("");
    const [role, setRole] = useState("");
    const [otp, setOtp] = useState("");
    const [captcha, setCaptcha] = useState("");

    const handleSubmit = (e) => {
        e.preventDefault();
        console.log({ phoneNumber, address, companyName, role, otp, captcha });
    };
    return(
        <div className="min-h-screen w-full grid grid-cols-1 md:grid-cols-2">
        {/* Left Branding Section */}
        <div className="hidden md:flex flex-col justify-center px-16 bg-slate-900 text-white">
            <h1 className="text-4xl font-semibold mb-4">
            Additional Details
            </h1>
            <p className="text-slate-300 max-w-md">
            Provide extra information to complete your profile.
            </p>
        </div>
        {/* Right Signup Details Section */}
        <div className="flex items-center justify-center bg-slate-50 px-6">
            <div className="w-full max-w-md">
            <h2 className="text-2xl font-semibold text-slate-800 mb-2">
                More Info
            </h2>
            <p className="text-sm text-slate-500 mb-8">
                Fill in the details below to proceed
            </p>
            <form onSubmit={handleSubmit} className="space-y-5">
                {/* Phone Number */}
                <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">
                    Phone Number
                </label>
                <input
                    type="tel"
                    value={phoneNumber}
                    onChange={(e) => setPhoneNumber(e.target.value)}
                    placeholder="Enter your phone number"
                    className="w-full px-3 py-2.5 rounded-md border border-slate-300
                            focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required
                />
                </div>
                {/*Verify Phone Number by OTP*/}
                <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">
                    OTP
                </label>
                <input
                    type="text"
                    value={otp}
                    onChange={(e) => setOtp(e.target.value)}
                    placeholder="Enter the OTP sent to your phone"
                    className="w-full px-3 py-2.5 rounded-md border border-slate-300
                            focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required
                />
                </div>
                {/* Address */}
                <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">
                    Address
                </label>
                <input
                    type="text"
                    value={address}
                    onChange={(e) => setAddress(e.target.value)}
                    placeholder="Enter your address"
                    className="w-full px-3 py-2.5 rounded-md border border-slate-300
                            focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required
                />

                </div>
                {/* Company Name */}
                <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">
                    Company Name
                </label>
                <input
                    type="text"
                    value={companyName}
                    onChange={(e) => setCompanyName(e.target.value)}
                    placeholder="Enter your company name"
                    className="w-full px-3 py-2.5 rounded-md border border-slate-300
                            focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required
                />
                </div>
                {/* Role/Position */}
                <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">
                    Role/Position
                </label>
                <input
                    type="text"
                    value={role}
                    onChange={(e) => setRole(e.target.value)}
                    placeholder="Enter your role or position"
                    className="w-full px-3 py-2.5 rounded-md border border-slate-300
                            focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required
                />
                </div>
                {/* CAPTCHA */}
                <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">
                    CAPTCHA
                </label>
                <input
                    type="text"
                    value={captcha}
                    onChange={(e) => setCaptcha(e.target.value)}
                    placeholder="Enter the CAPTCHA"
                    className="w-full px-3 py-2.5 rounded-md border border-slate-300
                            focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required
                />
                </div>
                {/* Submit Button */}
                <button
                type="submit"
                className="w-full bg-blue-600 text-white font-medium py-2.5 rounded-md
                        hover:bg-blue-700 transition"
                >
                Submit Details
                </button>
            </form>
            </div>
        </div>
        </div>
    );
}